export default async function loadFfmpeg() {
  const ffmpegModule = await import('@ffmpeg/ffmpeg');
  const { toBlobURL } = await import('@ffmpeg/util');

  const createFFmpeg = ffmpegModule.createFFmpeg || ffmpegModule.default?.createFFmpeg;
  const FFmpeg = ffmpegModule.FFmpeg || ffmpegModule.default?.FFmpeg;

  let ffmpeg;
  if (createFFmpeg) {
    ffmpeg = createFFmpeg({ log: true });
  } else if (FFmpeg) {
    ffmpeg = new FFmpeg();
  } else {
    throw new Error('❌ Neither createFFmpeg nor FFmpeg is available!');
  }

  const baseURL = 'https://cdn.jsdelivr.net/npm/@ffmpeg/core@latest/dist/umd';

  try {
    await ffmpeg.load({
      coreURL: `${baseURL}/ffmpeg-core.js`,
      wasmURL: `${baseURL}/ffmpeg-core.wasm`,
    });
    console.log("✅ ffmpeg loaded successfully!");
  } catch (error) {
    console.error("❌ Error loading ffmpeg:", error);
  }

  return ffmpeg;
}