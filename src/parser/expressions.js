
class Expr {

}

class Binary extends Expr {

	constructor(left, operator, right) {
		super()
  	this.left = left
  	this.operator = operator
  	this.right = right
  }		
}

class Grouping extends Expr {

	constructor(exprression) {
		super()
  	this.exprression = exprression
  }		
}

class Literal extends Expr {
	
	constructor(value) {
		super()
  	this.value = value
  }	
}

class Unary extends Expr {
	
	constructor(operator, right) {
		super()
  	this.operator = operator
  	this.right = right
  }	
}