/**
 * 
 */

package sg.gov.nhb.ihuayu.activity.db.entity;

/**
 * @author lixingwang
 */
public class ScenarioDialog {
    private String id;
    private String title_id;
    private int sentence_sequence_id;
    private String gender;
    private String narrator;
    private String sentence;
    private String sentence_en;
    private String sentence_py;
    private String audio;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle_id() {
        return title_id;
    }

    public void setTitle_id(String title_id) {
        this.title_id = title_id;
    }

    public int getSentence_sequence_id() {
        return sentence_sequence_id;
    }

    public void setSentence_sequence_id(int sentence_sequence_id) {
        this.sentence_sequence_id = sentence_sequence_id;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNarrator() {
        return narrator;
    }

    public void setNarrator(String narrator) {
        this.narrator = narrator;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public String getSentence_en() {
        return sentence_en;
    }

    public void setSentence_en(String sentence_en) {
        this.sentence_en = sentence_en;
    }

    public String getSentence_py() {
        return sentence_py;
    }

    public void setSentence_py(String sentence_py) {
        this.sentence_py = sentence_py;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }
}
