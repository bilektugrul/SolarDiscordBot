package io.github.bilektugrul.solardiscordbot.polls;

import java.util.List;

public class PollOption {

    private final int id;
    private final String option;

    private List<Long> voters;

    public PollOption(int id, String option, List<Long> voters) {
        this.id = id;
        this.option = option;
        this.voters = voters;
    }

    public int getId() {
        return id;
    }

    public String getOption() {
        return option;
    }

    public List<Long> getVoters() {
        return voters;
    }

    public void addVoter(long id) {
        voters.add(id);
    }

    public void removeVoter(long id) {
        voters.remove(id);
    }

    public void setVoters(List<Long> voters) {
        this.voters = voters;
    }
}