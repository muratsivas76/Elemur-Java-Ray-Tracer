case "GraniteMaterial": {
    Color baseColor = parseColorFromText(text, "baseColor");
    double roughness = parseDouble(text, "roughness");
    double specular = parseDouble(text, "specular");
    double reflectivity = parseDouble(text, "reflectivity");
    return new GraniteMaterial(baseColor, roughness, specular, reflectivity, new Matrix4());
} case "NazarMaterial": {
    Color blueColor = parseColorFromText(text, "blueColor");
    Color whiteColor = parseColorFromText(text, "whiteColor");
    double glowIntensity = parseDouble(text, "glowIntensity");
    double transparency = parseDouble(text, "transparency");
    double reflectivity = parseDouble(text, "reflectivity");
    return new NazarMaterial(blueColor, whiteColor, glowIntensity, transparency, reflectivity, new Matrix4());
} case "TurkishDelightMaterial": {
    Color primaryColor = parseColorFromText(text, "primaryColor");
    Color powderColor = parseColorFromText(text, "powderColor");
    double softness = parseDouble(text, "softness");
    double sweetness = parseDouble(text, "sweetness");
    double transparency = parseDouble(text, "transparency");
    return new TurkishDelightMaterial(primaryColor, powderColor, softness, sweetness, transparency, new Matrix4());
} case "SalmonMaterial": {
    Color fleshColor = parseColorFromText(text, "fleshColor");
    Color skinColor = parseColorFromText(text, "skinColor");
    Color fatColor = parseColorFromText(text, "fatColor");
    double freshness = parseDouble(text, "freshness");
    double oiliness = parseDouble(text, "oiliness");
    return new SalmonMaterial(fleshColor, skinColor, fatColor, freshness, oiliness, new Matrix4());
} case "MirrorMaterial": {
    Color tintColor = parseColorFromText(text, "tintColor");
    double reflectivity = parseDouble(text, "reflectivity");
    double sharpness = parseDouble(text, "sharpness");
    return new MirrorMaterial(tintColor, reflectivity, sharpness, new Matrix4());
} case "BaklavaMaterial": {
    Color pastryColor = parseColorFromText(text, "pastryColor");
    Color syrupColor = parseColorFromText(text, "syrupColor");
    Color nutColor = parseColorFromText(text, "nutColor");
    double layers = parseDouble(text, "layers");
    double syrupiness = parseDouble(text, "syrupiness");
    return new BaklavaMaterial(pastryColor, syrupColor, nutColor, layers, syrupiness, new Matrix4());
} case "SimitMaterial": {
    Color crustColor = parseColorFromText(text, "crustColor");
    Color sesameColor = parseColorFromText(text, "sesameColor");
    Color softColor = parseColorFromText(text, "softColor");
    double crispiness = parseDouble(text, "crispiness");
    double sesameDensity = parseDouble(text, "sesameDensity");
    return new SimitMaterial(crustColor, sesameColor, softColor, crispiness, sesameDensity, new Matrix4());
}
