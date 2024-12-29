import { useEffect } from 'react';

const NotFoundPage = () => {
  useEffect(() => {
    const body = document.body;
    const createStar = () => {
      const right = Math.random() * 500;
      const top = Math.random() * window.innerHeight;
      const star = document.createElement('div');

      star.classList.add('absolute', 'w-1', 'h-1', 'bg-white', 'rounded-full', 'star');
      body.appendChild(star);
      star.style.top = `${top}px`;
      let starRight = right;

      const runStar = setInterval(() => {
        if (starRight >= window.innerWidth) {
          clearInterval(runStar);
          star.remove();
        } else {
          starRight += 3;
          star.style.right = `${starRight}px`;
        }
      }, 10);
    };

    const starInterval = setInterval(createStar, 100);

    return () => {
      clearInterval(starInterval);
    };
  }, []);

  return (
    <div className="flex justify-center items-center h-screen bg-gradient-to-t from-[#2e1753] to-[#050819] relative overflow-hidden">
      <div className="absolute top-10 text-white text-center">
        <h5>ERROR</h5>
        <h1 className="text-5xl">404</h1>
        <hr className="my-4" />
        <p>Page Not Found</p>
      </div>
      <div className="absolute top-1/2 transform -translate-y-1/2 animate-astronautFly">
        <img
          src="https://images.vexels.com/media/users/3/152639/isolated/preview/506b575739e90613428cdb399175e2c8-space-astronaut-cartoon-by-vexels.png"
          alt="Astronaut"
          className="w-24"
        />
      </div>
      <style jsx>{`
        @keyframes astronautFly {
          0% {
            left: -100px;
          }
          25% {
            top: 50%;
            transform: rotate(30deg);
          }
          50% {
            transform: rotate(45deg);
            top: 55%;
          }
          75% {
            top: 60%;
            transform: rotate(30deg);
          }
          100% {
            left: 110%;
            transform: rotate(45deg);
          }
        }
        
        @keyframes starTwinkle {
          0% {
            background: rgba(255, 255, 255, 0.4);
          }
          25% {
            background: rgba(255, 255, 255, 0.8);
          }
          50% {
            background: rgba(255, 255, 255, 1);
          }
          75% {
            background: rgba(255, 255, 255, 0.8);
          }
          100% {
            background: rgba(255, 255, 255, 0.4);
          }
        }

        .animate-astronautFly {
          animation: astronautFly 6s infinite linear;
        }

        .star {
          animation: starTwinkle 3s infinite linear;
        }
      `}</style>
      <style jsx global>{`
        body {
          margin: 0;
          overflow: hidden; /* Prevent scrolling */
          height: 100vh; /* Make sure the height is set correctly */
        }
        *, *::before, *::after {
          box-sizing: border-box; /* Ensuring correct measurement calculations */
        }
      `}</style>
    </div>
  );
};

export default NotFoundPage;