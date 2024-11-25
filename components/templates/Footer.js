// components/Footer.js
import { useEffect, useRef } from 'react';
import * as THREE from 'three';
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls.js';
import { UnrealBloomPass } from 'three/examples/jsm/postprocessing/UnrealBloomPass.js';
import { RenderPass } from 'three/examples/jsm/postprocessing/RenderPass.js';
import { EffectComposer } from 'three/examples/jsm/postprocessing/EffectComposer.js';
import { PointerLockControls } from 'three/examples/jsm/controls/PointerLockControls.js';

const Footer = () => {
    const canvasRef = useRef(null);

    useEffect(() => {
        const canvas = canvasRef.current;
        const renderer = new THREE.WebGLRenderer({ canvas, antialias: true });
        renderer.setSize(window.innerWidth, window.innerHeight);
        renderer.setPixelRatio(window.devicePixelRatio);
        renderer.shadowMap.enabled = true;
        renderer.shadowMap.type = THREE.PCFSoftShadowMap;

        const camera = new THREE.PerspectiveCamera(70, window.innerWidth / window.innerHeight, 0.1, 1000);
        const scene = new THREE.Scene();
        scene.background = new THREE.Color(0x05071b); // رنگ پس‌زمینه

        const controls = new PointerLockControls(camera, document.body);

        // تنظیم نورها
        const pointLight_1 = new THREE.PointLight(0xDF71FF); // رنگ نور نقطه‌ای
        pointLight_1.intensity = 1.0; // کاهش شدت نور
        pointLight_1.distance = 16.5;
        pointLight_1.castShadow = true;

        const light2 = new THREE.PointLight(0xDF71FF); // رنگ نور دوم
        light2.intensity = 0.5; // کاهش شدت نور
        light2.position.set(0, 10, 0); // تنظیم موقعیت نور

        const renderPass = new RenderPass(scene, camera);
        const composer = new EffectComposer(renderer);
        const bloomPass = new UnrealBloomPass(new THREE.Vector2(window.innerWidth, window.innerHeight), 2.0, 0.6, 0.1);

        const curve = new THREE.CatmullRomCurve3([
            new THREE.Vector3(-50.5, 0.3, 50.5),
            new THREE.Vector3(50.5, 0.8, 50.5),
            new THREE.Vector3(50.5, 0.3, -50.5),
            new THREE.Vector3(-50.5, 0.8, -50.5),
        ]);
        curve.curveType = "centripetal";
        curve.closed = true;

        const tubeGeo = new THREE.TubeGeometry(curve, 100, 10, 10, true);
        const tubeMaterial = new THREE.MeshStandardMaterial({ color: 0x7a5af8 }); // رنگ لوله
        const wireframeMat = new THREE.MeshStandardMaterial({ color: 0x141b22, emissive: 0x1E2835, wireframe: true }); // رنگ و تابش وایرفریم
        const wireframeMesh = new THREE.Mesh(tubeGeo, wireframeMat);
        const tubeMesh = new THREE.Mesh(tubeGeo, tubeMaterial);
        tubeMesh.receiveShadow = true;

        const map = new THREE.TextureLoader().load("/resources/images/alphaTest.jpg");

        const sphereGeo = new THREE.SphereGeometry();
        let sphereMesh;

        if (map) {
            const sphereMat = new THREE.MeshStandardMaterial({ color: 0xDF71FF, emissive: 0x1E2835, alphaMap: map, alphaTest: 0.7 }); // رنگ و تابش کره
            sphereMesh = new THREE.Mesh(sphereGeo, sphereMat);
            sphereMesh.castShadow = true;
            scene.add(sphereMesh);
        }

        tubeMesh.add(wireframeMesh);
        const clock = new THREE.Clock();
        composer.addPass(renderPass);
        composer.addPass(bloomPass);
        scene.add(light2);
        scene.add(tubeMesh); // فقط tubeMesh را به صحنه اضافه می‌کنیم

        const orbit = new OrbitControls(camera, renderer.domElement);
        orbit.update();
        orbit.addEventListener("change", render);

        function onWindowResize() {
            camera.aspect = window.innerWidth / window.innerHeight;
            camera.updateProjectionMatrix();
            renderer.setSize(window.innerWidth, window.innerHeight);
        }

        window.addEventListener("resize", onWindowResize);

        let tyme = 0;

        function render() {
            tyme += 0.01;
            renderer.autoClear = false;
            renderer.clear();
            renderer.setPixelRatio(window.devicePixelRatio);

            const time = clock.getElapsedTime();
            const looptime = 50;
            const t = (time % looptime) / looptime;
            const t2 = ((time + 1) % looptime) / looptime;
            const t3 = ((time + 0.7) % looptime) / looptime;
            const pos = tubeMesh.geometry.parameters.path.getPointAt(t);
            const pos2 = tubeMesh.geometry.parameters.path.getPointAt(t2);
            const pos3 = tubeMesh.geometry.parameters.path.getPointAt(t3);

            light2.position.copy(pos3);
            camera.lookAt(pos);
            controls.getObject().position.copy(pos);

            if (sphereMesh) {
                sphereMesh.position.copy(pos2);
                sphereMesh.rotation.x += 0.01;
                sphereMesh.rotation.z += 0.01;
                sphereMesh.material.alphaTest = Math.abs(Math.sin(tyme) * 0.04) + 0.7;
            }

            composer.render();
        }

        function animate() {
            render();
            requestAnimationFrame(animate);
        }

        animate();

        return () => {
            window.removeEventListener("resize", onWindowResize);
            renderer.dispose();
        };
    }, []);

    return (
        <footer>
            <canvas ref={canvasRef} />
        </footer>
    );
};

export default Footer;