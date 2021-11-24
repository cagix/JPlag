package de.jplag;

/**
 * @author Emeric Kwemou
 * @date 22.01.2005
 */
public abstract class AbstractParser { // TODO TS: We should rename this class, as all concrete parsers shadow its name.
    protected ErrorReporting program;
    protected int errors = 0;
    private int errorsSum = 0;

    public boolean hasErrors() {
        return errors != 0;
    }

    public int errorsCount() {
        return errorsSum;
    }

    protected void parseEnd() {
        errorsSum += errors;
    }

    public ErrorReporting getProgram() {
        return program;
    }

    public void setProgram(ErrorReporting prog) {
        this.program = prog;
    }
}
