SELECT
        *
    FROM
    	people
    WHERE
        age = /*$age*/31
        AND (
        	name in /* %INCLUDE('./Sub.sql') */('Takashi', 'Masashi')
        	OR name in /* 
        		%INCLUDE('../../../sql/SQLManagerTest/Select.sql') */('Taro', 'Jiro')
        )
