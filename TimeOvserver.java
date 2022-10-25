import java.time.*;

public interface TimeOvserver {
    public void timeUpdate(LocalTime newTime);
    public void dateUpdate(LocalDate newDate);
}
