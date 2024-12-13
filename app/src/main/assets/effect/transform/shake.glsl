float druction = 5000.0;
float nowtime = 0.0;

vec4 getFromColor(vec2 uv) {
    return texture2D(texture, uv);
}
float Rand(vec2 v) {
    return fract(sin(dot(v.xy, vec2(12.9898, 78.233))) * 43758.5453);
}
float movexred = 0.01;
float value = 0.0;
float centerys[11];
float usepro = 5.0;


float getvalue() {
    value = 0.05 + value + Rand(vec2(usepro, ratio + value)) / 8.0;
    float zhengfu = Rand(vec2(usepro, ratio + value)) > 0.5 ? -1.0 : 1.0;
    return value * zhengfu;
}
float getnearx(float posy) {
    posy += Rand(vec2(usepro * nowtime, nowtime));
    posy = fract(posy);

    float rtn = 0.0;
    for (int i = 1;i < 11; i++) {
        if (posy < abs(centerys[i])) {
            float centery = abs(centerys[i]) + abs(centerys[i - 1]);
            centery /= 2.0;
            float cha = centery - abs(centerys[i - 1]);
            rtn = posy - centery;
            rtn = sqrt(cha * cha - rtn * rtn) / 8.0;
            if (centerys[i] < 0.0) {
                rtn *= -1.0;
            }
            break;
        }
    }
    return rtn;
}

vec4 effect(vec2 uv) {
    nowtime = mod(uTime, druction) / druction;
    centerys[0] = 0.0;
    centerys[1] = getvalue();
    centerys[2] = getvalue();
    centerys[3] = getvalue();
    centerys[4] = getvalue();
    centerys[5] = getvalue();
    centerys[6] = getvalue();
    centerys[7] = getvalue();
    centerys[8] = getvalue();
    centerys[9] = getvalue();
    centerys[10] = getvalue();


    vec2 texCoord = uv.xy / vec2(1.0).xy;
    vec4 colorgb = getFromColor(texCoord);

    float startmovex = getnearx(texCoord.y);

    movexred = movexred / ratio;
    float offx = 0.0;
    bool isin = fract(nowtime * 4.0 - texCoord.y) < 0.05;
    if (isin) {
        offx = Rand(vec2(nowtime, texCoord.y));
        offx = offx > 0.5 ? offx : -offx;
    }

    texCoord.x = texCoord.x + offx / 100.0 + startmovex;
    /**if (texCoord.x < 0.0 || texCoord.x > 1.0) {
        return vec4(0.0);
    }*/
    if (isin) {
        float sp = 10.0 * ratio;
        float cheng = texCoord.x * sp;
        float weiba = fract(cheng);
        float usex = cheng - weiba;
        usex /= sp;
        offx = Rand(vec2(usex * texCoord.y, texCoord.y * 1000.0));
        float panduan = 0.96;
        if (fract(nowtime * 50.0) > 0.1) {
            panduan = 0.99;
        }
        if (offx > panduan) {
            return mix(vec4(1.0), getFromColor(texCoord), weiba);
        }
    }

    texCoord.x = texCoord.x + startmovex;
    vec4 colorr = getFromColor(texCoord);
    float random = Rand(vec2(nowtime));
    if (random < 0.3) {
        colorgb.r = colorr.r;
    } else if (random < 0.5) {
        colorgb.g = colorr.g;
    } else {
        colorgb.b = colorr.b;
    }
    return colorgb;
}
