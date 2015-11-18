package com.example.sfarmani.ucsctutor.utils;

/**
 * Created by george on 11/4/15.
 * copied and modified from:
 * http://www.javapractices.com/topic/TopicAction.do?Id=5
 * and
 * http://www.javapractices.com/apps/movies/javadoc/src-html/hirondelle/movies/util/Util.html
 */

/**
 Utility methods for common argument validations.

 Replaces if statements at the start of a method with
 more compact method calls.

 Example use case.
 Instead of :
 
 public void doThis(String aText){
 if (!Util.textHasContent(aText)){
 throw new IllegalArgumentException();
 }
 //..main body elided
 }
 
 One may instead write :
 
 public void doThis(String aText){
 Args.checkForContent(aText);
 //..main body elided
 }
 
 */
public final class Args {


    public static void checkForContent(String aText, String argName){
        if( (aText == null) || (aText.trim().length() <= 0 ) ){
            throw new IllegalArgumentException(argName + " has no visible content");
        }
    }

    public static boolean hasContent(String aText){
        if( (aText == null) || (aText.trim().length() <= 0 ) ){
            return false;
        }else return true;
    }

    public static void checkForRange(int aNumber, int aLow, int aHigh, String argName) {
        if (aLow > aHigh){
            throw new IllegalArgumentException("Low (" + aLow + ") greater than High (" +aHigh+ ")");
        }
        if ( aNumber < aLow || aNumber > aHigh) {
            throw new IllegalArgumentException(argName + " (" + aNumber + ") not in range " + aLow + "  ... " + aHigh);
        }
    }

    /**
     If a Number is less than 1, then throw an
     IllegalArgumentException.
     */
    public static void checkForPositive(int aNumber, String argName) {
        if (aNumber < 1) {
            throw new IllegalArgumentException(argName + " (" +aNumber + ") is less than 1");
        }
    }


    /**
     If an Object is null, then throw a NullPointerException.

     Use cases :
     
     doSomething( Football aBall ){
     //1. call some method on the argument :
     //if aBall is null, then exception is automatically thrown, so
     //there is no need for an explicit check for null.
     aBall.inflate();

     //2. assign to a corresponding field (common in constructors):
     //if aBall is null, no exception is immediately thrown, so
     //an explicit check for null may be useful here
     Args.checkForNull( aBall );
     fBall = aBall;

     //3. pass on to some other method as parameter :
     //it may or may not be appropriate to have an explicit check
     //for null here, according the needs of the problem
     Args.checkForNull( aBall ); //??
     fReferee.verify( aBall );
     }
     
     */
    public static void checkForNull(Object aObject) {
        if (aObject == null) {
            throw new NullPointerException();
        }
    }

    // PRIVATE
    private Args(){
        //empty - prevent construction
    }
}