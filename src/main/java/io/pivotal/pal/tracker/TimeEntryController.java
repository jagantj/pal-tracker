package io.pivotal.pal.tracker;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;

@RestController
@RequestMapping("/time-entries")
public class TimeEntryController {

    private final TimeEntryRepository timeEntryRepository;
    private final DistributionSummary distributionSummary;

    private final Counter counter;

    @Autowired
    public TimeEntryController(TimeEntryRepository timeEntryRepository, MeterRegistry meterRegistry) {
        this.timeEntryRepository = timeEntryRepository;
        distributionSummary = meterRegistry.summary("timeEntry.summary");
        counter = meterRegistry.counter("timeEntry.actionCounter");
    }

    @PostMapping(produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity create(@RequestBody TimeEntry timeEntryToCreate) {
        final TimeEntry timeEntry = timeEntryRepository.create(timeEntryToCreate);
        counter.increment();
        distributionSummary.record(timeEntryRepository.list().size());
        return new ResponseEntity(timeEntry, HttpStatus.CREATED);
    }

    @GetMapping(value = "{timeEntryId}", produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<TimeEntry> read(@PathVariable long timeEntryId) {
        final TimeEntry timeEntry = timeEntryRepository.find(timeEntryId);
        return createOkOrNotFountResponseEntity(timeEntry);
    }

    @GetMapping(produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<TimeEntry>> list() {
        counter.increment();
        final List<TimeEntry> timeEntries = timeEntryRepository.list();
        return new ResponseEntity<>(timeEntries, HttpStatus.OK);
    }

    @PutMapping(value = "{timeEntryId}", produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity update(@PathVariable long timeEntryId, @RequestBody TimeEntry timeEntryToUpdate) {
        final TimeEntry timeEntry = timeEntryRepository.update(timeEntryId, timeEntryToUpdate);
        return createOkOrNotFountResponseEntity(timeEntry);
    }

    @DeleteMapping(value = "{timeEntryId}", produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity delete(@PathVariable long timeEntryId) {
        timeEntryRepository.delete(timeEntryId);
        counter.increment();
        distributionSummary.record(timeEntryRepository.list().size());
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    private ResponseEntity createOkOrNotFountResponseEntity(TimeEntry timeEntry) {
        if (timeEntry != null) {
            counter.increment();
            return new ResponseEntity<>(timeEntry, HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }
}
