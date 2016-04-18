package in.guclink.www.organizer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class InstanceIDListenerService extends Service {
    public InstanceIDListenerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
