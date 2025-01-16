import Input from '@/components/modules/Input';
import MainLayout from '@/layouts/MainLayout';
import React, { useState } from 'react';

function Convert({ supportedFormats }) {


  return (
    <MainLayout
      pageTitle="Image Format Converter"
      searchQuery={searchQuery}
      setSearchQuery={setSearchQuery}
      handleSearch={handleSearch}
      className="app w-full flex flex-col relative"
      mainTagClassName="main w-full py-4 px-80"
    >

    </MainLayout>
  );
}

// Fetch supported formats using getStaticProps
export async function getStaticProps() {
  // Assume supported formats are loaded from an API or another source
  const supportedFormats = ['jpg', 'png', 'gif'];

  return {
    props: {
      supportedFormats,
    },
    revalidate: 10, // The page will be revalidated every 10 seconds
  };
}

export default Convert;