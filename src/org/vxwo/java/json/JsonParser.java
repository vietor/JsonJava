package org.vxwo.java.json;

class JsonParser {
	enum Token {
		None, Curly_Open, Curly_Close, Squared_Open, Squared_Close, Colon, Comma, String, Number, True, False, Null
	}

	private int index;
	private char[] json;
	private Token lookAheadToken = Token.None;

	JsonParser(String json) {
		this.json = json.toCharArray();
	}

	JsonValue decode() throws JsonException {
		return parseValue();
	}

	private JsonValue parseObject() throws JsonException {
		JsonValue obj = new JsonValue();

		consumeToken(); // {

		while (true) {
			switch (LookAhead()) {

			case Comma:
				consumeToken();
				break;

			case Curly_Close:
				consumeToken();
				return obj;

			default: {

				// name
				String name = parseString();

				// :
				if (nextToken() != Token.Colon) {
					throw new JsonException("Expected colon at index " + index);
				}

				// value
				obj.setMember(name, parseValue());
			}
				break;
			}
		}
	}

	private JsonValue parseArray() throws JsonException {
		JsonValue array = new JsonValue(JsonType.Array, null);

		consumeToken(); // [

		while (true) {
			switch (LookAhead()) {

			case Comma:
				consumeToken();
				break;

			case Squared_Close:
				consumeToken();
				return array;

			default: {
				array.append(parseValue());
			}
				break;
			}
		}
	}

	private JsonValue parseValue() throws JsonException {
		switch (LookAhead()) {
		case Number:
			return parseNumber();

		case String:
			return new JsonValue(parseString());

		case Curly_Open:
			return parseObject();

		case Squared_Open:
			return parseArray();

		case True:
			consumeToken();
			return new JsonValue(true);

		case False:
			consumeToken();
			return new JsonValue(false);

		case Null:
			consumeToken();
			return new JsonValue(JsonType.Null, null);
		}

		throw new JsonException("Unrecognized token at index" + index);
	}

	private String parseString() throws JsonException {
		consumeToken(); // "

		StringBuilder s = new StringBuilder();

		int runIndex = -1;

		while (index < json.length) {
			char c = json[index++];

			if (c == '"') {
				if (runIndex != -1) {
					if (s.length() == 0)
						return new String(json, runIndex, index - runIndex - 1);

					s.append(json, runIndex, index - runIndex - 1);
				}
				return s.toString();
			}

			if (c != '\\') {
				if (runIndex == -1)
					runIndex = index - 1;

				continue;
			}

			if (index == json.length)
				break;

			if (runIndex != -1) {
				s.append(json, runIndex, index - runIndex - 1);
				runIndex = -1;
			}

			switch (json[index++]) {
			case '"':
				s.append('"');
				break;

			case '\\':
				s.append('\\');
				break;

			case '/':
				s.append('/');
				break;

			case 'b':
				s.append('\b');
				break;

			case 'f':
				s.append('\f');
				break;

			case 'n':
				s.append('\n');
				break;

			case 'r':
				s.append('\r');
				break;

			case 't':
				s.append('\t');
				break;

			case 'u': {
				int remainingLength = json.length - index;
				if (remainingLength < 4)
					break;

				// parse the 32 bit hex into an integer codepoint
				long codePoint = parseUnicode(json[index], json[index + 1],
						json[index + 2], json[index + 3]);
				s.append((char) codePoint);

				// skip 4 chars
				index += 4;
			}
				break;
			}
		}

		throw new JsonException("Unexpectedly reached end of String");
	}

	private long parseSingleChar(char c1, long multipliyer) {
		long p1 = 0;
		if (c1 >= '0' && c1 <= '9')
			p1 = (long) (c1 - '0') * multipliyer;
		else if (c1 >= 'A' && c1 <= 'F')
			p1 = (long) ((c1 - 'A') + 10) * multipliyer;
		else if (c1 >= 'a' && c1 <= 'f')
			p1 = (long) ((c1 - 'a') + 10) * multipliyer;
		return p1;
	}

	private long parseUnicode(char c1, char c2, char c3, char c4) {
		long p1 = parseSingleChar(c1, 0x1000);
		long p2 = parseSingleChar(c2, 0x100);
		long p3 = parseSingleChar(c3, 0x10);
		long p4 = parseSingleChar(c4, 1);
		return p1 + p2 + p3 + p4;
	}

	private JsonValue parseNumber() throws JsonException {
		consumeToken();

		// Need to start back one place because the first digit is also a token
		// and would have been consumed
		int startIndex = index - 1;

		do {
			char c = json[index];

			if ((c >= '0' && c <= '9') || c == '.' || c == '-' || c == '+'
					|| c == 'e' || c == 'E') {
				if (++index == json.length)
					throw new JsonException(
							"Unexpected end of String whilst parsing number");
				continue;
			}

			break;
		} while (true);

		String number = new String(json, startIndex, index - startIndex);

		JsonValue result = null;
		if (number.indexOf('.') != -1 || number.indexOf('e') != -1
				|| number.indexOf('E') != -1) {
			try {
				double value_double = Double.parseDouble(number);
				result = new JsonValue(value_double);
			} catch (NumberFormatException e) {
			}
		}
		if (result == null) {
			try {
				int value_int = Integer.parseInt(number);
				result = new JsonValue(value_int);
			} catch (NumberFormatException e) {
			}
		}
		if (result == null) {
			try {
				long value_long = Long.parseLong(number);
				result = new JsonValue(value_long);
			} catch (NumberFormatException e) {
			}
		}
		if (result == null)
			result = new JsonValue(0);
		return result;
	}

	private Token LookAhead() throws JsonException {
		if (lookAheadToken != Token.None)
			return lookAheadToken;

		return lookAheadToken = nextTokenCore();
	}

	private void consumeToken() {
		lookAheadToken = Token.None;
	}

	private Token nextToken() throws JsonException {
		Token result = lookAheadToken != Token.None ? lookAheadToken
				: nextTokenCore();

		lookAheadToken = Token.None;

		return result;
	}

	private Token nextTokenCore() throws JsonException {
		char c;

		// Skip head whitespace
		if (index == 0) {
			do {
				c = json[index];

				if (c == '{')
					break;
				if (c < (char) 0x80 && c != ' ' && c != '\t' && c != '\n'
						&& c != '\r')
					break;

			} while (++index < json.length);

			if (index == json.length) {
				throw new JsonException("Reached end of string unexpectedly");
			}
		}
		// Skip past whitespace
		do {
			c = json[index];

			if (c > ' ')
				break;
			if (c != ' ' && c != '\t' && c != '\n' && c != '\r')
				break;

		} while (++index < json.length);

		if (index == json.length) {
			throw new JsonException("Reached end of String unexpectedly");
		}

		c = json[index];

		index++;

		switch (c) {
		case '{':
			return Token.Curly_Open;

		case '}':
			return Token.Curly_Close;

		case '[':
			return Token.Squared_Open;

		case ']':
			return Token.Squared_Close;

		case ',':
			return Token.Comma;

		case '"':
			return Token.String;

		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
		case '-':
		case '+':
		case '.':
			return Token.Number;

		case ':':
			return Token.Colon;

		case 'f':
			if (json.length - index >= 4 && json[index + 0] == 'a'
					&& json[index + 1] == 'l' && json[index + 2] == 's'
					&& json[index + 3] == 'e') {
				index += 4;
				return Token.False;
			}
			break;

		case 't':
			if (json.length - index >= 3 && json[index + 0] == 'r'
					&& json[index + 1] == 'u' && json[index + 2] == 'e') {
				index += 3;
				return Token.True;
			}
			break;

		case 'n':
			if (json.length - index >= 3 && json[index + 0] == 'u'
					&& json[index + 1] == 'l' && json[index + 2] == 'l') {
				index += 3;
				return Token.Null;
			}
			break;

		}

		throw new JsonException("Could not find token at index " + --index);
	}
}
