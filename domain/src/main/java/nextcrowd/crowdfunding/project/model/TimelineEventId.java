package nextcrowd.crowdfunding.project.model;

import com.github.f4b6a3.uuid.UuidCreator;

public record TimelineEventId(String id) implements Comparable<TimelineEventId> {

    public static TimelineEventId generate(){
        return new TimelineEventId(UuidCreator.getTimeOrderedEpoch().toString());
    }

    @Override
    public int compareTo(TimelineEventId o) {
        return this.id.compareTo(o.id);
    }

}
