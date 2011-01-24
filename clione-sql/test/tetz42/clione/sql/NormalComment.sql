SELECT /*+ ORDERD */
    *
FROM /*! MYSQL */
    EMPLOYEES
WHERE
	ABC = /*$TAKO*/'tako'
	OR EFG = /*TAKO*/'tako'
	OR HIJ = /**TAKO*/'tako'
