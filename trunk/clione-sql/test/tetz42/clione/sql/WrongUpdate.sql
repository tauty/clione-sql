	UPDATE people set
		name = CONCAT(trim(name), '2')
		age = 10000
WHERE
        age = /*$age*/31
