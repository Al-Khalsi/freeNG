import { useEffect, useRef, useState } from 'react';

const notFound = () => {

  const canvasRef = useRef(null);
  const [collapse, setCollapse] = useState(false);
  const [expanse, setExpanse] = useState(false);
  const stars = useRef([]);
  const maxorbit = 255;
  const centerx = 0;
  const centery = 0;

  useEffect(() => {
    const canvas = canvasRef.current;
    const context = canvas.getContext('2d');
    const h = canvas.height;
    const w = canvas.width;

    const setDPI = (canvas, dpi) => {
      const scaleFactor = dpi / 96;
      canvas.width = Math.ceil(canvas.width * scaleFactor);
      canvas.height = Math.ceil(canvas.height * scaleFactor);
      context.scale(scaleFactor, scaleFactor);
    };

    const rotate = (cx, cy, x, y, angle) => {
      const radians = angle;
      const cos = Math.cos(radians);
      const sin = Math.sin(radians);
      const nx = (cos * (x - cx)) + (sin * (y - cy)) + cx;
      const ny = (cos * (y - cy)) - (sin * (x - cx)) + cy;
      return [nx, ny];
    };

    const star = function() {
      const rands = [Math.random() * (maxorbit / 2) + 1, Math.random() * (maxorbit / 2) + maxorbit];
      this.orbital = rands.reduce((p, c) => p + c, 0) / rands.length;
      this.x = centerx;
      this.y = centery + this.orbital;
      this.yOrigin = centery + this.orbital;
      this.speed = (Math.floor(Math.random() * 2.5) + 1.5) * Math.PI / 180;
      this.rotation = 0;
      this.startRotation = (Math.floor(Math.random() * 360) + 1) * Math.PI / 180;
      this.id = stars.current.length;
      this.collapseBonus = this.orbital - (maxorbit * 0.7);
      this.collapseBonus = this.collapseBonus < 0 ? 0 : this.collapseBonus;
      stars.current.push(this);
      this.color = `rgba(255,255,255,${1 - (this.orbital / 255)})`;
      this.hoverPos = centery + (maxorbit / 2) + this.collapseBonus;
      this.expansePos = centery + (this.id % 100) * -10 + (Math.floor(Math.random() * 20) + 1);
      this.prevR = this.startRotation;
      this.prevX = this.x;
      this.prevY = this.y;
    };

    star.prototype.draw = function() {
      if (!expanse) {
        this.rotation = this.startRotation + (performance.now() / 50) * this.speed;
        if (!collapse) {
          if (this.y > this.yOrigin) {
            this.y -= 2.5;
          }
          if (this.y < this.yOrigin - 4) {
            this.y += (this.yOrigin - this.y) / 10;
          }
        } else {
          if (this.y > this.hoverPos) {
            this.y -= (this.hoverPos - this.y) / -5;
          }
          if (this.y < this.hoverPos - 4) {
            this.y += 2.5;
          }
        }
      } else {
        this.rotation = this.startRotation + (performance.now() / 50) * (this.speed / 2);
        if (this.y > this.expansePos) {
          this.y -= Math.floor(this.expansePos - this.y) / -140;
        }
      }

      context.save();
      context.fillStyle = this.color;
      context.strokeStyle = this.color;
      context.beginPath();
      const oldPos = rotate(centerx, centery, this.prevX, this.prevY, -this.prevR);
      context.moveTo(oldPos[0], oldPos[1]);
      context.translate(centerx, centery);
      context.rotate(this.rotation);
      context.translate(-centerx, -centery);
      context.lineTo(this.x, this.y);
      context.stroke();
      context.restore();

      this.prevR = this.rotation;
      this.prevX = this.x;
      this.prevY = this.y;
    };

    const initStars = () => {
      for (let i = 0; i < 2500; i++) {
        new star();
      }
    };

    const loop = () => {
      context.fillStyle = 'rgba(25,25,25,0.2)';
      context.fillRect(0, 0, w, h);
      stars.current.forEach((star) => star.draw());
      requestAnimationFrame(loop);
    };

    setDPI(canvas, 192);
    initStars();
    loop();
  }, [collapse, expanse]);

  return (
    <div className="relative w-full h-full bg-black">
      <canvas ref={canvasRef} className="absolute inset-0" />
      <div
        className={`absolute left-1/2 top-1/2 transform -translate-x-1/2 -translate-y-1/2 w-64 h-64 rounded-full flex items-center justify-center cursor-pointer transition-all duration-500 ${expanse ? 'opacity-0 pointer-events-none' : ''}`}
        onClick={() => {
          setCollapse(false);
          setExpanse(true);
        }}
        onMouseEnter={() => !expanse && setCollapse(true)}
        onMouseLeave={() => !expanse && setCollapse(false)}
      >
        <span className={`text-gray-600 text-lg relative transition-all duration-500`}>
          <span className="before:block before:w-4 before:h-px before:bg-gray-600 before:absolute before:-left-4 before:top-1/2 before:transform before:-translate-y-1/2" />
          ENTER
          <span className="after:block after:w-4 after:h-px after:bg-gray-600 after:absolute after:-right-4 after:top-1/2 after:transform after:-translate-y-1/2" />
        </span>
      </div>
    </div>
  );
};

export default notFound