import Particles, { initParticlesEngine } from "@tsparticles/react";
import { useEffect, useMemo, useState } from "react";
import { loadSlim } from "@tsparticles/slim";

const ParticlesComponent = (props) => {
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
                    value: "#fff", // رنگ پس‌زمینه
                },
            },
            fpsLimit: 60,
            interactivity: {
                events: {
                    onClick: {
                        enable: false,
                        mode: "push", // افزودن ذرات جدید به هنگام کلیک
                    },
                    onHover: {
                        enable: true,
                        mode: "none", // دفع ذرات به هنگام حرکت ماوس
                    },
                },
                modes: {
                    push: {
                        quantity: 4, // تعداد ذرات جدید در حالت push
                    },
                    repulse: {
                        distance: 100, // فاصله دفع ذرات
                        duration: 0.4, // مدت زمان
                    },
                    grab: {
                        distance: 150, // فاصله جذب ذرات
                    },
                },
            },
            particles: {
                color: {
                    value: "#FFFFFF" // آرایه رنگ برای انتخاب تصادفی
                },
                links: {
                    color: "#000", // رنگ خطوط
                    distance: 200,
                    enable: true,
                    opacity: 0.5, // شفافیت خطوط
                    width: 0.3,
                },
                move: {
                    direction: "none",
                    enable: true,
                    outModes: {
                        default: "bounce", // بازگشت ذرات به داخل
                    },
                    random: true,
                    speed: 3, // افزایش سرعت
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
                    value: 300, // تعداد کل ذرات
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
                    type: "circle", // اشکال متنوع
                    options: {
                        polygon: {
                            sides: 5, // تعداد اضلاع برای اشکال چندضلعی
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

    return <Particles id={props.id} init={particlesLoaded} options={options} />;
};

export default ParticlesComponent;