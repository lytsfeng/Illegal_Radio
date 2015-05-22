package com.ldkj.illegal_radio.fragments.base;

import com.ldkj.illegal_radio.models.IllegalRadioModel;
import com.ldkj.illegal_radio.utils.Attribute;

/**
 * Created by john on 15-4-10.
 */
public interface OnFragmentInteractionListener {
    void updateThreshold(int att);
    boolean setCommand(String pCommand);
    float[] getMeasureValue(Attribute.DATATYPE datatype);
    boolean startTask(Attribute.TASKTYPE tasktype);
    void stopTask(Attribute.TASKTYPE tasktype);
    IllegalRadioModel updateIllegal(IllegalRadioModel model,boolean isAdd);

}