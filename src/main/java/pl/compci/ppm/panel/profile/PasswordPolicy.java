package pl.compci.ppm.panel.profile;

/**
 * Created by TKrolikowski on 2015-12-01.
 */
public class PasswordPolicy {

    private int minLength = 10;

    private int minCapital = 1;

    private int minNumbers = 1;

    private int minSpecial = 1;

    public int getMinLength() {
        return minLength;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    public int getMinCapital() {
        return minCapital;
    }

    public void setMinCapital(int minCapital) {
        this.minCapital = minCapital;
    }

    public int getMinNumbers() {
        return minNumbers;
    }

    public void setMinNumbers(int minNumbers) {
        this.minNumbers = minNumbers;
    }

    public int getMinSpecial() {
        return minSpecial;
    }

    public void setMinSpecial(int minSpecial) {
        this.minSpecial = minSpecial;
    }
}
