package us.duoproject;

public class MathUtil {

    public static double clampedMap(double p_144852_, double p_144853_, double p_144854_, double p_144855_, double p_144856_) {
        return clampedLerp(p_144855_, p_144856_, inverseLerp(p_144852_, p_144853_, p_144854_));
    }

    private static double clampedLerp(double p_14086_, double p_14087_, double p_14088_) {
        if (p_14088_ < 0.0D) {
            return p_14086_;
        } else {
            return p_14088_ > 1.0D ? p_14087_ : lerp(p_14088_, p_14086_, p_14087_);
        }
    }

    private static double inverseLerp(double p_14113_, double p_14114_, double p_14115_) {
        return (p_14113_ - p_14114_) / (p_14115_ - p_14114_);
    }

    private static double lerp(double p_14140_, double p_14141_, double p_14142_) {
        return p_14141_ + p_14140_ * (p_14142_ - p_14141_);
    }
}
