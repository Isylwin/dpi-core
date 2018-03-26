package nl.oscar.dpi.library.model;

public enum BankName {
    ABN(0), ING(1), RAB(2);

    private int i;

    BankName(int i) {
        this.i = i;
    }

    public static BankName fromOrdinal(int i) {
        for (BankName name : BankName.values()) {
            if (i == name.i) {
                return name;
            }
        }

        return null;
    }
}
