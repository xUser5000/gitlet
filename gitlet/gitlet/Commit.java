package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  @author xUser5000
 */
public class Commit implements Serializable, Dumpable, Comparable<Commit> {
    private final String message;
    private final Date timestamp;
    /** Original parent from the  */
    private final String parent;
    private final String secondaryParent;

    private final Map<String, String> trackedFiles;
    private final String hash;

    private Commit(String message, Date timestamp, String parent, String secondaryParent, Map<String, String> trackedFiles) {
        this.message = message;
        this.timestamp = (timestamp != null) ? timestamp : new Date();
        this.parent = parent;
        this.secondaryParent = secondaryParent;
        this.trackedFiles = (trackedFiles != null) ? trackedFiles : new TreeMap<>();
        this.hash = generateHash();
    }

    @Override
    public int compareTo(Commit other) {
        return this.getHash().compareTo(other.getHash());
    }

    /* Builder class */
    public static class Builder {
        private String message;
        private Date timestamp;
        private String parent;
        private String secondaryParent;
        private Map<String, String> trackedFiles;

        public Builder(String message) {
            this.message = message;
        }

        public Builder timestamp(Date timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder parent(String parent) {
            this.parent = parent;
            return this;
        }

        public Builder secondaryParent(String secondaryParent) {
            this.secondaryParent = secondaryParent;
            return this;
        }

        public Builder trackedFiles(Map<String, String> trackedFiles) {
            this.trackedFiles = trackedFiles;
            return this;
        }

        public Commit build() {
            return new Commit(message, timestamp, parent, secondaryParent, trackedFiles);
        }
    }

    private String generateHash() {
        List<Object> hashItems = new ArrayList<>();
        hashItems.add(message);
        hashItems.add(timestamp.toString());
        hashItems.add((parent == null) ? "" : parent);
        hashItems.add((secondaryParent == null) ? "" : secondaryParent);
        for (Map.Entry<String, String> entry: trackedFiles.entrySet()) {
            hashItems.add(entry.toString());
        }
        return sha1(hashItems);
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getParent() {
        return parent;
    }

    public String getSecondaryParent() {
        return secondaryParent;
    }

    public Map<String, String> getTrackedFiles() {
        return trackedFiles;
    }

    public String getHash() {
        return hash;
    }

    public void saveCommit(File commitsDirectory) {
        File commitFile = join(commitsDirectory, hash);
        writeObject(commitFile, this);
    }

    public String log() {
        StringBuilder builder = new StringBuilder();
        builder.append("===\n");
        builder.append(String.format("commit %s\n", getHash()));
        if (getParent() != null && getSecondaryParent() != null) {
            builder.append(
                    String.format("Merge: %s %s\n",  getParent().substring(0, 7),  getSecondaryParent().substring(0, 7))
            );
        }
        Formatter formatter = new Formatter().format("Date: %1$ta %1$tb %1$td %1$tT %1$tY %1$tz", getTimestamp());
        String formattedDate = formatter.toString();
        builder.append(String.format("%s\n", formattedDate));
        builder.append(String.format("%s\n\n", getMessage()));
        return builder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Commit) {
            return ((Commit) obj).getHash().equals(this.getHash());
        }
        return false;
    }

    @Override
    public String toString() {
        return "Commit{" +
                "timestamp=" + timestamp +
                ", message='" + message + '\'' +
                ", parent='" + parent + '\'' +
                ", secondaryParent='" + secondaryParent + '\'' +
                ", trackedFiles=" + trackedFiles +
                ", hash='" + hash + '\'' +
                '}';
    }

    @Override
    public void dump() {
        System.out.println(this);
    }

}
