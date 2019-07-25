package io.pivotal.pal.tracker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class InMemoryTimeEntryRepository implements TimeEntryRepository {

    private Map<Long, TimeEntry> dataRepository = new HashMap<>();

    private Long timeId = 1L;

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        Long timeEntryId = timeId++;
        final TimeEntry createdTimeEntry = new TimeEntry(timeEntryId, timeEntry.getProjectId(), timeEntry.getUserId(),
            timeEntry.getDate(), timeEntry.getHours());

        dataRepository.put(timeEntryId, createdTimeEntry);
        return createdTimeEntry;
    }

    @Override
    public TimeEntry find(Long id) {
        return dataRepository.values().stream().filter(e -> e.getId() == id).findFirst().orElse(null);
    }

    @Override
    public TimeEntry update(Long id, TimeEntry timeEntry) {
        if (find(id) == null) {
            return null;
        }
        TimeEntry updatedTimeEntry = new TimeEntry(id, timeEntry.getProjectId(), timeEntry.getUserId(),
            timeEntry.getDate(), timeEntry.getHours());
        dataRepository.replace(id, updatedTimeEntry);
        return updatedTimeEntry;
    }

    @Override
    public void delete(Long id) {
        dataRepository.remove(id);
    }

    @Override
    public List<TimeEntry> list() {
        return dataRepository.values().stream().collect(Collectors.toList());
    }
}
