package cookbook.auth;

public enum LoginReturn {
  SUCCESS,
  INVALID_USERNAME,
  INVALID_PASSWORD,
  SQL_ERROR,
  UNKNOWN_ERROR
}