package gui;

import javafx.scene.control.TextField;

/**
 * Created by ado on 05.12.15.
 */
public class NumericTextField extends TextField{

    private int maxValue = 0;

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }
    public int getMaxValue(){
        return maxValue;
    }


    @Override
    public void replaceText(int start, int end, String text){
        if(validate(text))
            if(maxValueCondition( getText().replace(getText().substring(start,end),text) ))
                super.replaceText(start, end, text);
            else
                this.setText(String.valueOf(maxValue));
    }
    @Override
    public void replaceSelection(String text){
        if(validate(text))
            if(maxValueCondition(getText().replace(getSelectedText(),text)))
                super.replaceSelection(text);
            else
                this.setText(String.valueOf(maxValue));

    }
    private boolean validate(String text){
        return ( ("").equals(text) || text.matches("[0-9]") );
    }
    private boolean maxValueCondition(String text){
        if(!"".equals(text)){
            if(maxValue>0 && Integer.valueOf(text)<= maxValue){
                return true;
            }
        }else
        return true;

        return false;
    }

}
