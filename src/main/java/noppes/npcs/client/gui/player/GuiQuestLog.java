package noppes.npcs.client.gui.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import noppes.npcs.NBTTags;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.QuestLogData;
import noppes.npcs.client.Client;
import noppes.npcs.client.CustomNpcResourceListener;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.TextBlockClient;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.constants.EnumPlayerPacket;
import org.lwjgl.opengl.GL11;
import tconstruct.client.tabs.InventoryTabQuests;
import tconstruct.client.tabs.TabRegistry;

import java.util.*;

public class GuiQuestLog extends GuiNPCInterface implements ITopButtonListener,ICustomScrollListener, IGuiData{

	private final ResourceLocation resource = new ResourceLocation("customnpcs","textures/gui/standardbg.png");

    private EntityPlayer player;
    private GuiCustomScroll scroll;
	private HashMap<Integer,GuiMenuSideButton> sideButtons = new HashMap<Integer,GuiMenuSideButton>();
	private QuestLogData data = new QuestLogData();
	private boolean noQuests = false;
	private boolean questDetails = true;

    private HashMap<String,String> questAlertsOnOpen;
    private String trackedQuestKeyOnOpen;

	private Minecraft mc = Minecraft.getMinecraft();

	public GuiQuestLog(EntityPlayer player) {
		super();
		this.player = player;
        xSize = 280;
        ySize = 180;
        NoppesUtilPlayer.sendData(EnumPlayerPacket.QuestLog);
        drawDefaultBackground = false;
	}
    public void initGui(){
        super.initGui();
    	sideButtons.clear();
        guiTop +=10;

		TabRegistry.addTabsToList(buttonList);
		TabRegistry.updateTabValues(guiLeft, guiTop, InventoryTabQuests.class);

        noQuests = false;

        if(data.categories.isEmpty()){
        	noQuests = true;
        	return;
        }
        List<String> categories = new ArrayList<String>();
        categories.addAll(data.categories.keySet());
        Collections.sort(categories,String.CASE_INSENSITIVE_ORDER);
        int i = 0;
        for(String category : categories){
        	if(data.selectedCategory.isEmpty())
        		data.selectedCategory = category;
        	sideButtons.put(i, new GuiMenuSideButton(i,guiLeft - 69, this.guiTop +2 + i*21, 70,22, category));
        	i++;
        }
        sideButtons.get(categories.indexOf(data.selectedCategory)).active = true;

        if(scroll == null)
        	scroll = new GuiCustomScroll(this,0);

        scroll.setList(data.categories.get(data.selectedCategory));
        scroll.setSize(134, 174);
        scroll.guiLeft = guiLeft + 5;
        scroll.guiTop = guiTop + 15;
        addScroll(scroll);

        addButton(new GuiButtonNextPage(1, guiLeft + 286, guiTop + 176, true));
        addButton(new GuiButtonNextPage(2, guiLeft + 144, guiTop + 176, false));

        GuiNpcButton trackingButton = new GuiNpcButton(3, guiLeft + 260, guiTop + 151, 50, 20, new String[]{"quest.track", "quest.tracking"}, data.trackedQuestKey.equals(data.selectedCategory + ":" + data.selectedQuest) ? 1 : 0);
        if (trackingButton.displayString.equals("quest.tracking")) {
            trackingButton.packedFGColour = 0x32CD32;
        }
        addButton(trackingButton);

        GuiNpcButton alertButton = new GuiNpcButton(4, guiLeft + 205, guiTop + 151, 50, 20, new String[]{"quest.alerts", "quest.noAlerts"}, data.getQuestAlerts() ? 0 : 1);
        addButton(alertButton);

        getButton(1).visible = questDetails && data.hasSelectedQuest();
        getButton(2).visible = !questDetails && data.hasSelectedQuest();
        getButton(3).visible = !data.selectedQuest.isEmpty() && getButton(1).visible;
        getButton(4).visible = getButton(3).visible;
    }
    @Override
	protected void actionPerformed(GuiButton guibutton){
    	if(guibutton.id == 1){
    		questDetails = false;
    	}
    	if(guibutton.id == 2){
    		questDetails = true;
    	}
        if(guibutton.id == 3){
            if (!data.trackedQuestKey.equals(data.selectedCategory + ":" + data.selectedQuest)) {
                data.trackedQuestKey = data.selectedCategory + ":" + data.selectedQuest;
            } else {
                data.trackedQuestKey = "";
            }
        }
        if(guibutton.id == 4){
            data.toggleQuestAlerts();
        }
        initGui();
    }
    @Override
    public void drawScreen(int i, int j, float f){
    	if(scroll != null)
    		scroll.visible = !noQuests;
    	drawDefaultBackground();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(resource);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, 252, 195);
        drawTexturedModalRect(guiLeft + 252, guiTop, 188, 0, 67, 195);
        super.drawScreen(i, j, f);

        if(noQuests){
        	fontRendererObj.drawString(StatCollector.translateToLocal("quest.noquests"),guiLeft + 84,guiTop + 80, CustomNpcResourceListener.DefaultTextColor);
        	return;
        }
        for(GuiMenuSideButton button: sideButtons.values().toArray(new GuiMenuSideButton[sideButtons.size()])){
        	button.drawButton(mc, i, j);
        }
    	fontRendererObj.drawString(data.selectedCategory,guiLeft + 5,guiTop + 5, CustomNpcResourceListener.DefaultTextColor);

        if(!data.hasSelectedQuest())
        	return;

        if (questDetails) {
        	drawProgress();
        	String title = StatCollector.translateToLocal("gui.text");
        	fontRendererObj.drawString(title, guiLeft + 284 - fontRendererObj.getStringWidth(title), guiTop + 179, CustomNpcResourceListener.DefaultTextColor);
        } else {
        	drawQuestText();
        	String title = StatCollector.translateToLocal("quest.objectives");
        	fontRendererObj.drawString(title, guiLeft + 168, guiTop + 179, CustomNpcResourceListener.DefaultTextColor);
        }

        GL11.glPushMatrix();
        GL11.glTranslatef(guiLeft + 148, guiTop, 0);
        GL11.glScalef(1.24f, 1.24f, 1.24f);
        fontRendererObj.drawString(data.selectedQuest, (130 - fontRendererObj.getStringWidth(data.selectedQuest)) / 2, 4, CustomNpcResourceListener.DefaultTextColor);
        GL11.glPopMatrix();
        drawHorizontalLine(guiLeft + 142, guiLeft + 312, guiTop + 17,  + 0xFF000000 + CustomNpcResourceListener.DefaultTextColor);
    }

    private void drawQuestText(){
    	TextBlockClient block = new TextBlockClient(data.getQuestText(), 174, true, player);
        int yoffset = guiTop + 5;
    	for(int i = 0; i < block.lines.size(); i++){
    		String text = block.lines.get(i).getFormattedText();
    		fontRendererObj.drawString(text, guiLeft + 142, guiTop + 20 + (i * fontRendererObj.FONT_HEIGHT), CustomNpcResourceListener.DefaultTextColor);
    	}
    }

    private void drawProgress() {
        String complete = data.getComplete();
        if(complete != null && !complete.isEmpty())
        	fontRendererObj.drawString(StatCollector.translateToLocalFormatted("quest.completewith", complete), guiLeft + 144, guiTop + 105, CustomNpcResourceListener.DefaultTextColor);

    	int yoffset = guiTop + 22;
        for(String process : data.getQuestStatus()){
        	int index = process.lastIndexOf(":");
        	if(index > 0){
        		String name = process.substring(0, index);
        		String trans = StatCollector.translateToLocal(name);
        		if(!trans.equals(name))
        			name = trans;
        		trans = StatCollector.translateToLocal("entity." + name + ".name");
        		if(!trans.equals("entity." + name + ".name")){
        			name = trans;
        		}
        		process = name + process.substring(index);
        	}
        	fontRendererObj.drawString("- " + process, guiLeft + 144, yoffset , CustomNpcResourceListener.DefaultTextColor);
	        yoffset += 10;
        }
	}

	protected void drawGuiContainerBackgroundLayer(float f, int i, int j)
    {
    }
    @Override
    public void mouseClicked(int i, int j, int k)
    {
    	super.mouseClicked(i, j, k);
        if (k == 0){
        	if(scroll != null)
        		scroll.mouseClicked(i, j, k);
            for (GuiMenuSideButton button : new ArrayList<GuiMenuSideButton>(sideButtons.values())){
                if (button.mousePressed(mc, i, j)){
                	sideButtonPressed(button);
                }
            }
        }
    }
    private void sideButtonPressed(GuiMenuSideButton button) {
    	if(button.active)
    		return;
    	NoppesUtil.clickSound();
        data.selectedCategory = button.displayString;
        data.selectedQuest = "";
        this.initGui();
    }
	@Override
	public void customScrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
		if(!scroll.hasSelected())
			return;
		data.selectedQuest = scroll.getSelected();
		initGui();
	}

    @Override
    public void keyTyped(char c, int i)
    {
        if (i == 1 || i == mc.gameSettings.keyBindInventory.getKeyCode()) // inventory key
        {
            mc.displayGuiScreen(null);
            mc.setIngameFocus();
        }
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }
	@Override
	public void setGuiData(NBTTagCompound compound) {
		QuestLogData data = new QuestLogData();
		data.readNBT(compound);
		this.data = data;
        this.questAlertsOnOpen = new HashMap<>(data.questAlerts);
        this.trackedQuestKeyOnOpen = data.trackedQuestKey;
		initGui();
	}

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        this.save();
    }

    @Override
	public void save() {
        if (this.data != null &&
                (!Objects.equals(this.questAlertsOnOpen, data.questAlerts) ||
                !Objects.equals(this.trackedQuestKeyOnOpen, data.trackedQuestKey))) {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setTag("Alerts", NBTTags.nbtStringStringMap(data.questAlerts));
            Client.sendData(EnumPacketServer.QuestLogToServer, compound, this.data.trackedQuestKey);
        }
	}
	
}
