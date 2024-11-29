import { useEffect, useState } from "react";
import { TbPng } from "react-icons/tb";

const Footer = () => {
    const [iconCount, setIconCount] = useState(0);

    const calculateIconCount = () => {
        const width = window.innerWidth;
        const iconWidth = 45;
        const count = Math.floor(width / iconWidth);
        setIconCount(count);
    };

    useEffect(() => {
        calculateIconCount();
        window.addEventListener("resize", calculateIconCount);
        return () => {
            window.removeEventListener("resize", calculateIconCount);
        };
    }, []);

    return (
        <footer className="footer w-full h-48 mt-8">
            <section className="relative flex flex-col w-full h-full overflow-hidden bg-bgDarkGray">
                <div className="absolute left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2
                text-5xl bg-bgDarkGray text-clDarkBlue flex justify-center items-center">PixelFreebies</div>
                {Array.from({ length: 8 }, (_, indexRow) => (
                    <div key={indexRow} className="flex flex-nowrap w-full text-clDarkBlue">
                        {Array.from({ length: iconCount }, (_, index) => (
                            <TbPng key={index} className="text-5xl -my-3 flex-grow" />
                        ))}
                    </div>
                ))}
            </section>
        </footer>
    );
};

export default Footer;