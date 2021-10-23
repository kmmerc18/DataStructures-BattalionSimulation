public class Main {
    public static void main(String[] args) {
        System.out.println("Deploying troops...");
        Settings settings = new Settings(args);

        System.out.println(String.format("---End of Day---\n" +
                "Battles: %d", settings.getBattles()));
        if(settings.getGenEval()) settings.genEval();
        if(settings.getWatcher()) settings.watchOut();
    }
}

