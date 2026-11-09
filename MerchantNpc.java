/** Mbok Rukmini, a market trader who gives gameplay tips. */
public class MerchantNpc extends Npc {

    public MerchantNpc() {
        super("Mbok Rukmini", "npc/Merchant.png");
    }

    @Override
    protected String[] getLines() {
        return new String[]{
            "Di dalam candi, kayu penghalang hanya bisa ditebas Raden (SPACE).",
            "Peti berat juga hanya bisa didorong Raden.",
            "Sasaran di seberang air? Biarkan Dyah memanahnya.",
            "Tekan Q kapan saja untuk berganti pahlawan."
        };
    }
}
