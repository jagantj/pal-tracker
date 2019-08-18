package io.pivotal.pal.tracker;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;

public class JdbcTimeEntryRepository implements TimeEntryRepository {

    private JdbcTemplate jdbcTemplate;

    public JdbcTimeEntryRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        final GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        String insertQuery = "INSERT INTO time_entries (project_id, user_id, date, hours) " + "VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(con -> {
            final PreparedStatement preparedStatement = con.prepareStatement(insertQuery, RETURN_GENERATED_KEYS);

            preparedStatement.setLong(1, timeEntry.getProjectId());
            preparedStatement.setLong(2, timeEntry.getUserId());
            preparedStatement.setDate(3, Date.valueOf(timeEntry.getDate()));
            preparedStatement.setInt(4, timeEntry.getHours());
            return preparedStatement;

        }, generatedKeyHolder);
        return find(generatedKeyHolder.getKey().longValue());
    }

    @Override
    public TimeEntry find(Long id) {
        String selectQuery = "SELECT id, project_id, user_id, date, hours FROM time_entries WHERE id = ?";

        return jdbcTemplate.query(selectQuery, new Object[] {id}, createResultSetExtractor);
    }

    @Override
    public TimeEntry update(Long id, TimeEntry timeEntry) {
        String updateQuery = "UPDATE time_entries SET project_id = ?, user_id = ?, date = ?,  hours = ? WHERE id = ?";
        jdbcTemplate.update(updateQuery, timeEntry.getProjectId(), timeEntry.getUserId(),
            Date.valueOf(timeEntry.getDate()), timeEntry.getHours(), id);
        return find(id);
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM time_entries WHERE id = ?", id);
    }

    @Override
    public List<TimeEntry> list() {
        return jdbcTemplate.query("SELECT id, project_id, user_id, date, hours FROM time_entries", mapper);
    }

    private final RowMapper<TimeEntry> mapper = (rs, rowNum) -> new TimeEntry(rs.getLong("id"),
        rs.getLong("project_id"), rs.getLong("user_id"), rs.getDate("date").toLocalDate(), rs.getInt("hours"));

    private final ResultSetExtractor<TimeEntry> createResultSetExtractor =
            (rs) -> rs.next() ? mapper.mapRow(rs, 1) : null;

}
