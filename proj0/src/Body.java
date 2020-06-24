public class Body {
  public double xxPos;
  public double yyPos;
  public double xxVel;
  public double yyVel;
  public double mass;
  public String imgFileName;

  public static final double G = 6.67e-11;

  public Body(double xP, double yP, double xV, double yV, double m, String img) {
    xxPos = xP;
    yyPos = yP;
    xxVel = xV;
    yyVel = yV;
    mass = m;
    imgFileName = img;
  }

  public Body(Body b) {
    this.xxPos = b.xxPos;
    this.yyPos = b.yyPos;
    this.xxVel = b.xxVel;
    this.yyVel = b.yyVel;
    this.mass = b.mass;
    this.imgFileName = b.imgFileName;
  }

  public double calcDistance(Body b) {
    double dX = this.xxPos - b.xxPos;
    double dY = this.yyPos - b.yyPos;

    return Math.sqrt(dX * dX + dY * dY);
  }

  public double calcForceExertedBy(Body b) {
    double distance = this.calcDistance(b);

    return (G * this.mass * b.mass) / (distance * distance);
  }

  public double calcForceExertedByX(Body b) {
    double dX = b.xxPos - this.xxPos;
    double force = this.calcForceExertedBy(b);
    double distance = this.calcDistance(b);

    return force * dX / distance;
  }

  public double calcForceExertedByY(Body b) {
    double dY = b.yyPos - this.yyPos;
    double force = this.calcForceExertedBy(b);
    double distance = this.calcDistance(b);

    return force * dY / distance;
  }

  public double calcNetForceExertedByX(Body[] bodies) {
    double netForceX = 0;

    for (Body b : bodies) {
      if (!b.equals(this)) netForceX += this.calcForceExertedByX(b);
    }

    return netForceX;
  }

  public double calcNetForceExertedByY(Body[] bodies) {
    double netForceY = 0;

    for (Body b : bodies) {
      if (!b.equals(this)) netForceY += this.calcForceExertedByY(b);
    }

    return netForceY;
  }

  public void update(double dT, double fX, double fY) {
    double acceX = fX / this.mass;
    double acceY = fY / this.mass;

    this.xxVel += dT * acceX;
    this.yyVel += dT * acceY;
    this.xxPos += dT * this.xxVel;
    this.yyPos += dT * this.yyVel;
  }

  public void draw() {
    StdDraw.picture(this.xxPos, this.yyPos, this.imgFileName);
  }
}
