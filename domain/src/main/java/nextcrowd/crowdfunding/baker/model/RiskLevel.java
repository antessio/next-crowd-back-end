package nextcrowd.crowdfunding.baker.model;

public enum RiskLevel {
    HIGH(5), MED_HIGH(4), MODERATE(3), MED_LOW(2), LOW(1);

    private final int level;

    RiskLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
