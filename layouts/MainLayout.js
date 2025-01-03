import Header from '@/components/templates/Header';
import Footer from '@/components/templates/Footer';

const MainLayout = ({ children, className, mainTagClassName, searchQuery, setSearchQuery, handleSearch }) => {
    return (
        <div className={className}>
            <Header
                searchQuery={searchQuery}
                setSearchQuery={setSearchQuery}
                handleSearch={handleSearch}
            />
            <main className={mainTagClassName}>
                {children}
            </main>
            <Footer />
        </div>
    );
};

export default MainLayout;