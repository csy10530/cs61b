public class NBody {
  public static double readRadius(String file) {
    In in = new In(file);
    int num = in.readInt();
    double radius = in.readDouble();

    return radius;
  }

  public static Body[] readBodies(String file) {
    In in = new In(file);
    int num = in.readInt();
    double radius = in.readDouble();
    Body[] bodies = new Body[num];


    for (int i = 0; i < num; i++) {
      double xP = in.readDouble();
      double yP = in.readDouble();
      double xVel = in.readDouble();
      double yVel = in.readDouble();
      double mass = in.readDouble();
      String img = "images/" + in.readString();

      bodies[i] = new Body(xP, yP, xVel, yVel, mass, img);
    }

    return bodies;
  }

  public static void main(String[] args) {
    double T = Double.parseDouble(args[0]);
    double dt = Double.parseDouble(args[1]);
    String filename = args[2];
    String imageToDraw = "images/starfield.jpg";
    double radius = readRadius(filename);
    Body[] bodies = readBodies(filename);

    StdDraw.enableDoubleBuffering();

    StdDraw.setScale(-radius, radius);

    double currentTime = 0;
    while (currentTime < T) {
      double[] xForces = new double[bodies.length];
      double[] yForces = new double[bodies.length];

      for (int i = 0; i < bodies.length; i++) {
        xForces[i] = bodies[i].calcNetForceExertedByX(bodies);
        yForces[i] = bodies[i].calcNetForceExertedByY(bodies);
      }

      for (int j = 0; j < bodies.length; j++) bodies[j].update(dt, xForces[j], yForces[j]);

      StdDraw.picture(0, 0, imageToDraw);
      for (Body body : bodies) body.draw();
      StdDraw.show();
      StdDraw.pause(10);

      currentTime += dt;
    }

    StdOut.printf("%d\n", bodies.length);
    StdOut.printf("%.2e\n", radius);
    for (Body body : bodies) {
      StdOut.printf("%11.4e %11.4e %11.4e %11.4e %11.4e %12s\n",
              body.xxPos, body.yyPos, body.xxVel,
              body.yyVel, body.mass, body.imgFileName);
    }
  }
}
