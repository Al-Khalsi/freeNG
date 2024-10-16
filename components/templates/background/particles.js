import Particles, { initParticlesEngine } from "@tsparticles/react";
import { useEffect, useMemo, useState } from "react";
import { loadSlim } from "@tsparticles/slim";

const ParticlesComponent = ({ id, isDarkMode }) => {
    const [init, setInit] = useState(false);

    useEffect(() => {
        initParticlesEngine(async (engine) => {
            await loadSlim(engine);
        }).then(() => {
            setInit(true);
        });
    }, []);

    const particlesLoaded = (container) => {
        console.log(container);
    };

    const options = useMemo(
        () => ({
            background: {
                color: {
                    value:  isDarkMode ? "#121212" : "#ffffff",
                },
            },
            fpsLimit: 60,
            interactivity: {
                events: {
                    onClick: {
                        enable: false,
                        mode: "push", 
                    },
                    onHover: {
                        enable: true,
                        mode: "none", 
                    },
                },
                modes: {
                    push: {
                        quantity: 4, 
                    },
                    repulse: {
                        distance: 100, 
                        duration: 0.4, 
                    },
                    grab: {
                        distance: 150, 
                    },
                },
            },
            particles: {
                color: {
                    value: "#FFFFFF" 
                },
                links: {
                    color: isDarkMode ? '#ffffff' : '#000000',
                    distance: 200,
                    enable: true,
                    opacity: 0.5, 
                    width: 0.3,
                    // shadow: { 
                    //     enable: true,
                    //     color: "#000", 
                    //     blur: 5, 
                    //     offset: {
                    //         x: 0, 
                    //         y: 0, 
                    //     },
                    // },
                },
                move: {
                    direction: "none",
                    enable: true,
                    outModes: {
                        default: "bounce", 
                    },
                    random: true,
                    speed: 3, 
                    straight: false,
                    attract: {
                        enable: false,
                        rotate: {
                            x: 600,
                            y: 1200,
                        },
                    },
                },
                number: {
                    density: {
                        enable: true,
                        area: 800,
                    },
                    value: 300, 
                },
                opacity: {
                    value: 0.8,
                    animation: {
                        enable: true,
                        speed: 1,
                        minimumValue: 0.1,
                        sync: false,
                    },
                },
                shape: {
                    type: "circle", 
                    options: {
                        polygon: {
                            sides: 5, 
                        },
                    },
                },
                size: {
                    value: { min: 0, max: 0 },
                    animation: {
                        enable: true,
                        speed: 3,
                        minimumValue: 1,
                        sync: false,
                    },
                },
            },
            detectRetina: true,
        }),
        [],
    );

    return <Particles id={id} init={particlesLoaded} options={options} />;
};

export default ParticlesComponent;