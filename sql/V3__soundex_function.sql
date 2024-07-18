
CREATE OR REPLACE FUNCTION SOUNDEX(text) RETURNS text AS $$
DECLARE
    in_str ALIAS FOR $1;
    out_str TEXT = '';
    c CHAR;
    last_code CHAR;
    this_code CHAR;
BEGIN
    IF in_str IS NULL OR in_str = '' THEN
        RETURN NULL;
    END IF;

    -- Uppercase the input string
    in_str := UPPER(in_str);

    -- Process the first letter
    out_str := SUBSTRING(in_str FROM 1 FOR 1);
    last_code := CASE out_str
        WHEN 'B' THEN '1'
        WHEN 'F' THEN '1'
        WHEN 'P' THEN '1'
        WHEN 'V' THEN '1'
        WHEN 'C' THEN '2'
        WHEN 'G' THEN '2'
        WHEN 'J' THEN '2'
        WHEN 'K' THEN '2'
        WHEN 'Q' THEN '2'
        WHEN 'S' THEN '2'
        WHEN 'X' THEN '2'
        WHEN 'Z' THEN '2'
        WHEN 'D' THEN '3'
        WHEN 'T' THEN '3'
        WHEN 'L' THEN '4'
        WHEN 'M' THEN '5'
        WHEN 'N' THEN '5'
        WHEN 'R' THEN '6'
        ELSE NULL
    END;

    -- Process the rest of the string
    FOR i IN 2..LENGTH(in_str) LOOP
        c := SUBSTRING(in_str FROM i FOR 1);
        this_code := CASE c
            WHEN 'B' THEN '1'
            WHEN 'F' THEN '1'
            WHEN 'P' THEN '1'
            WHEN 'V' THEN '1'
            WHEN 'C' THEN '2'
            WHEN 'G' THEN '2'
            WHEN 'J' THEN '2'
            WHEN 'K' THEN '2'
            WHEN 'Q' THEN '2'
            WHEN 'S' THEN '2'
            WHEN 'X' THEN '2'
            WHEN 'Z' THEN '2'
            WHEN 'D' THEN '3'
            WHEN 'T' THEN '3'
            WHEN 'L' THEN '4'
            WHEN 'M' THEN '5'
            WHEN 'N' THEN '5'
            WHEN 'R' THEN '6'
            ELSE NULL
        END;
        IF this_code IS NOT NULL AND this_code != last_code THEN
            out_str := out_str || this_code;
        END IF;
        IF LENGTH(out_str) = 4 THEN
            RETURN out_str;
        END IF;
        last_code := this_code;
    END LOOP;

    -- Pad with zeroes if necessary
    RETURN out_str || REPEAT('0', 4 - LENGTH(out_str));
END;
$$ LANGUAGE plpgsql;
