package nextcrowd.crowdfunding.project.model;

import com.github.f4b6a3.uuid.UuidCreator;

import nextcrowd.crowdfunding.common.ValueObjectId;

public class TimelineEventId extends ValueObjectId {

    public TimelineEventId(String value) {
        super(value);
    }

    public static TimelineEventId generate(){
        return new TimelineEventId(UuidCreator.getTimeOrderedEpoch().toString());
    }


}
