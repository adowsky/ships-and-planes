package gui.component;

import javafx.scene.control.TextField;

import java.io.Serializable;

/**
 * TextFiled limited only to numeric signs.
 */
public class NumericTextField extends TextField implements Serializable{

    private int maxValue = Integer.MAX_VALUE;

    /**
     * Sets maximum value that can be written in the field
     * @param maxValue maximum value
     */
    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    /**
     * Returns maximum value that can be written in the field
     * @return maximum value that can be written in the field
     */
    public int getMaxValue(){
        return maxValue;
    }


    @Override
    public void replaceText(int start, int end, String text){
        String current = getText();
        if(validate(text))
            if(maxValueCondition(current.substring(0,start)+text+current.substring(end,current.length())) )
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

    /**
     * Validates text that wants to be placed in the field.
     * @param text text that wants to be placed in the field
     * @return  if text is valid.
     */
    private boolean validate(String text){
        return ( ("").equals(text) || text.matches("[0-9]") );
    }

    /**
     * checks if after addition of new element the value will be lower than max value.
     * @param text new Value.
     * @return if new value is lower than max value.
     */
    private boolean maxValueCondition(String text){
        if(!"".equals(text)){
            int newValue=0;
            try{
                newValue = Integer.valueOf(text);
            }catch(NumberFormatException ex){
               // ex.printStackTrace();
                return false;
            }
            if(maxValue>0 && newValue<= maxValue){
                return true;
            }
        }else
            return true;

        return false;
    }

}
