package twim.melsecplc.core.message.e;


import twim.melsecplc.core.message.e.qheader.ErrorInformationSection;

/**
 * @author liumin
 */
public class AbnormalCompletionException extends Exception {

    private ErrorInformationSection errorInformationSection;

    public AbnormalCompletionException(ErrorInformationSection errorInformationSection) {
        super("Abnormal completion. " + errorInformationSection.toString());
        this.errorInformationSection = errorInformationSection;
    }

    public ErrorInformationSection getErrorInformationSection() {
        return errorInformationSection;
    }
}
