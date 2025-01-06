package nextcrowd.crowdfunding.baker.model;

public enum RiskLevel {
    HIGH(5), MED_HIGH(4), MODERATE(3), MED_LOW(2), LOW(1);

    private final int level;

    RiskLevel(int level) {
        this.level = level;
    }

    public static RiskLevel fromLevel(int riskLevel) {
        switch (riskLevel) {
            case 5:
                return HIGH;
            case 4:
                return MED_HIGH;
            case 3:
                return MODERATE;
            case 2:
                return MED_LOW;
            case 1:
                return LOW;
            default:
                throw new IllegalArgumentException("Invalid risk level: " + riskLevel);
        }
    }

    public int getLevel() {
        return level;
    }
}
