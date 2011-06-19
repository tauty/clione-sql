SELECT
        *
    FROM
    	people
    WHERE
        age = /*$age*/31
        AND (
        	name in /* %include './Sub.sql' */('Takashi', 'Masashi')
        	OR name in /* 
        		%include('../../../sql/SQLManagerTest/Select.sql') */('Taro', 'Jiro')
        )
