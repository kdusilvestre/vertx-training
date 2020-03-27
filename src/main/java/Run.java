import com.kdusilvestre.vertx.EventBusRec;
import com.kdusilvestre.vertx.EventLoopVerticle;
import com.kdusilvestre.vertx.WorkerVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

import java.util.Objects;

public class Run {

    final String runMessage = "Somthing is not right! Bye";

    // Convenience method so you can run it in your IDE
    public static void main(String[] args) {
        new Run().start();
    }

    /* Deploys a number of event loop instances to handle your routes and the same number of background worker vertices */
    private void deployForEventLoop(int instances) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(EventBusRec.class.getName(), new DeploymentOptions().setWorker(true).setInstances(instances));
        vertx.deployVerticle(EventLoopVerticle.class.getName(), new DeploymentOptions().setInstances(instances));
    }

    /* Deploys a number of worker verticle instances to handle your routes */
    private void deployForWorkerVerticles(int instances) {
        Vertx.vertx().deployVerticle(WorkerVerticle.class.getName(), new DeploymentOptions().setWorker(true).setInstances(instances));
    }

    private void exit() {
        System.out.println(runMessage);
        System.exit(1);
    }

    public void start() {
        String instS = Objects.isNull(System.getProperty("VERTX_INSTANCES"))? "2" : System.getProperty("VERTX_INSTANCES");
        String mode = Objects.isNull(System.getProperty("VERTX_MODE"))? "e" : System.getProperty("VERTX_MODE");
        String modeReadable = "";
        try {

            int instances = Integer.valueOf(instS);
            if (mode.toLowerCase().equals("w")) {
                deployForWorkerVerticles(instances);
                modeReadable = "Worker";
            } else if (mode.toLowerCase().equals("e")) {
                deployForEventLoop(instances);
                modeReadable = "Event Loop";
            } else {
                exit();
            }
        } catch (Exception e) {
            exit();
        }

        System.out.println("Vert.x deployed on port 8443, SSL and HTTP2 enabled, with " + instS + " instance, using " +modeReadable+ " verticles to handle your endpoints ");
    }

}
