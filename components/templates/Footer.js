import { FaImage } from "react-icons/fa";

const Footer = () => {
    return (
        <footer className="footer w-full h-48">
            <section className="relative flex flex-col w-full h-full bg-bgDarkGray overflow-hidden">
                {Array.from({ length: 20 }, (_, rowIndex) => (
                    <div key={rowIndex} className="row relative -top-52 flex w-full py-2 text-6xl whitespace-nowrap -rotate-45">
                        {Array.from({ length: 30 }, (_, index) => (
                            <FaImage key={index}
                                className="px-1 hover:text-clPurple hover:text-shadow transition duration-500 select-none" />
                        ))}
                    </div>
                ))}
            </section>
        </footer>
    );
};

export default Footer;