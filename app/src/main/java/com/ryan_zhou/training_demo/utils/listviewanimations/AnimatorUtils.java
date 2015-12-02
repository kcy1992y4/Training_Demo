package com.ryan_zhou.training_demo.utils.listviewanimations;

import android.animation.Animator;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/11/2 10:56
 * @copyright TCL-MIE
 */
public class AnimatorUtils {

    public static Animator[] concatAnimators(final Animator[] childAnimators, final Animator[] animators, final
    Animator alphaAnimator) {
        Animator[] allAnimators = new Animator[childAnimators.length + animators.length + 1];
        System.arraycopy(childAnimators, 0, allAnimators, 0, childAnimators.length);
        System.arraycopy(animators, 0, allAnimators, childAnimators.length, animators.length);
        allAnimators[allAnimators.length - 1] = alphaAnimator;
        return allAnimators;
    }
}
