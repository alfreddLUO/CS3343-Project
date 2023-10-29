package GoGoEat;

import java.time.*;

public interface TimeObserver {
    public void timeUpdate(LocalTime newTime);

    public void dateUpdate(LocalDate newDate);
}
