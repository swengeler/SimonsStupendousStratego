package project.stratego.ai.mcts.logger;


public final class Logger {

	@SuppressWarnings("unused")
	private static final String logger = null;

  public static boolean DEBUG_OUTPUT_CALL_INFO = true;


  /**
   * Adds the call information to the current message (Where called from (Class, Method, Linenumber)
   * 
   * @param message
   * @return
   */
  private static final String concatCallInfo(String message) {
    return String.format("%-60s : %s", getCallInfo(4), message);
  }

  /**
   * 
   * @param depth
   * @return String representing call information from the stack at a specified depth
   */
	@SuppressWarnings("unused")
	public static String getCallInfo(int depth) {
    StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

    if (depth >= stackTraceElements.length) {
      return "Requested depth outside scope";
    }
    
    String callInfo = null;

    StackTraceElement previousMethod = stackTraceElements[depth];
    // System.out.println(previousMethod);

    if (previousMethod == null) {
      return "Strange call, cannot find caller";
    }

    String fileName = previousMethod.getFileName();
    String methodName = previousMethod.getMethodName();
    int lineNumber = previousMethod.getLineNumber();

    fileName = fileName == null ? "unknown" : fileName;
    methodName = methodName == null ? "unknown" : methodName;

    // callInfo = String.format("%s::%s:%d", fileName.replace(".java", ""), previousMethod.toString(), lineNumber);
    callInfo = String.format("%s::%s:%d", "", previousMethod.toString(), lineNumber);

    // return callInfo;
    return previousMethod.toString();
  }

  /**
   * Outputs the given message to the debug-level logger
   * 
   * @param output
   * @param message
   */
  public static final void debug(String message) {

    if (DEBUG_OUTPUT_CALL_INFO)
      message = concatCallInfo(message);

    System.out.println(message);
  }

  /**
   * See {@link Logger#debug(boolean, String)}, but formats the given argument using the format
   * 
   * @param output
   * @param format
   * @param args
   */
  public static final void debug(final boolean output, String format, Object... args) {
    if (!output)
      return;

    String message = format(format, args);

    if (DEBUG_OUTPUT_CALL_INFO)
      message = concatCallInfo(message);

    System.out.println(message);
  }

  /**
   * Outputs the given message to the log-level logger
   * 
   * @param output
   * @param message
   */
  public static final void log(final boolean output, String message) {
    if (!output)
      return;

    if (DEBUG_OUTPUT_CALL_INFO)
      message = concatCallInfo(message);

    System.out.println(message);
  }

  /**
   * Delegates to {@link Logger#log(boolean, String)}, but formats the given argument using the
   * format
   * 
   * @param output
   * @param format
   * @param args
   */
  public static final void log(final boolean output, String format, Object... args) {
    if (!output)
      return;

    String message = format(format, args);

    if (DEBUG_OUTPUT_CALL_INFO)
      message = concatCallInfo(message);

    System.out.println(message);
  }

  /**
   * Outputs the given message to the error-level logger
   * 
   * @param output
   * @param message
   */
  public static final void error(final boolean output, String message) {
    if (!output)
      return;

    if (DEBUG_OUTPUT_CALL_INFO)
      message = concatCallInfo(message);

    System.out.println(message);
  }

  /**
   * Delegates to {@link Logger#error(boolean, String)}, but formats the given argument using the
   * format
   * 
   * @param output
   * @param format
   * @param args
   */
  public static final void error(final boolean output, String format, Object... args) {
    if (!output)
      return;

    String message = format(format, args);

    if (DEBUG_OUTPUT_CALL_INFO)
      message = concatCallInfo(message);

    System.out.println(message);

  }

  /**
   * Used to produce a String formatted according to {@link String#format(String, Object...)}.
   * 
   * @param format
   * @param args
   * @return
   */
  public static final String format(String format, Object... args) {

    return String.format(format, args);
  }

  /**
   * Outputs the given message to the debug-level logger
   * 
   * @param output
   * @param message
   * @param error
   */
  public static final void debug(final boolean output, String message, Exception error) {
    if (!output)
      return;

    if (DEBUG_OUTPUT_CALL_INFO)
      message = concatCallInfo(message);

    System.out.println(message);

  }

  /**
   * 
   * Outputs the given message to the log-level logger
   * 
   * @param output
   * @param message
   * @param error
   */
  public static final void log(final boolean output, String message, Exception error) {
    if (!output)
      return;

    if (DEBUG_OUTPUT_CALL_INFO)
      message = concatCallInfo(message);

    System.out.println(message);
    System.out.println("Exception " + error);
  }

  /**
   * 
   * Outputs the given message to the error-level logger
   * 
   * @param output
   * @param message
   * @param error
   */
  public static final void error(final boolean output, String message, Exception error) {
    if (!output)
      return;

    if (DEBUG_OUTPUT_CALL_INFO)
      message = concatCallInfo(message);

    System.out.println(message);
  }

  public static final void println(String message) {
    if (DEBUG_OUTPUT_CALL_INFO)
      message = concatCallInfo(message);
    System.out.println(message);
  }

}
