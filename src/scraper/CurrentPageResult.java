package scraper;

import java.util.ArrayList;

public class CurrentPageResult {
    private ArrayList<String> internalLinks;
    private ArrayList<String> externalLinks;
    private ArrayList<String> phoneNumbers;
    private ArrayList<String> emails;
    private ArrayList<String> dates;
    private ArrayList<String> facebookLinks;
    private boolean changed = false; // so the UI thread can wait until CurrentPageResult has been updated to look at it again, rather than keep polling it's data
    private boolean finalPage = false; // to let the program know that the final page has been reached

    public boolean isFinalPage() {
        return finalPage;
    }

    public void setFinalPage(boolean finalPage) {
        this.finalPage = finalPage;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public boolean isChanged() {
        return changed;
    }

    public ArrayList<String> getInternalLinks() {
        return internalLinks;
    }

    public void setInternalLinks(ArrayList<String> internalLinks) {
        this.internalLinks = internalLinks;
    }

    public ArrayList<String> getExternalLinks() {
        return externalLinks;
    }

    public void setExternalLinks(ArrayList<String> externalLinks) {
        this.externalLinks = externalLinks;
    }

    public ArrayList<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(ArrayList<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public ArrayList<String> getEmails() {
        return emails;
    }

    public void setEmails(ArrayList<String> emails) {
        this.emails = emails;
    }

    public ArrayList<String> getDates() {
        return dates;
    }

    public void setDates(ArrayList<String> dates) {
        this.dates = dates;
    }

    public ArrayList<String> getFacebookLinks() {
        return facebookLinks;
    }

    public void setFacebookLinks(ArrayList<String> facebookLinks) {
        this.facebookLinks = facebookLinks;
    }
}
