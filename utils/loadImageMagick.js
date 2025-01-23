import { initializeImageMagick } from "@imagemagick/magick-wasm";

export default async function loadImageMagick() {
  return new Promise((resolve, reject) => {
    initializeImageMagick((magick) => {
      if (magick) {
        console.log("✅ ImageMagick WASM loaded successfully!");
        resolve(magick);
      } else {
        console.error("❌ Error loading ImageMagick WASM!");
        reject(new Error("Failed to load ImageMagick"));
      }
    });
  });
}
