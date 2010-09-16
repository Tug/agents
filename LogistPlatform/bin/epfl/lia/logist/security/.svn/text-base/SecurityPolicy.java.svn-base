package epfl.lia.logist.security;

/* Java utility import */
import java.util.ArrayList;


/**
 * A security rule set represents a simple policy. The
 * rules specify the rights that allow or deny access to
 * a resource from a specific threadgroup.
 */
public class SecurityPolicy {

	/**
	 * A list of rules
	 */
	private ArrayList<SecurityRule> maListOfRules = null;


	/**
	 * Default constructor of the class.
	 */
	public SecurityPolicy() {
		init();
	}

	
	/**
	 * Initialializes the rule set, by creating an
	 * empty security list.
	 */
	public void init() {
		if ( maListOfRules != null )
			maListOfRules.clear();
		else
			maListOfRules = new ArrayList<SecurityRule>();
	}

	
	/**
	 * Removes all the rules from the rule set.
	 */
	public void shutdown() {
		if ( maListOfRules != null )
			maListOfRules.clear();
		maListOfRules = null;
	}

	
	/**
	 * Appends a new rule to the policy.
	 */
	public void append( SecurityRule rule ) {
		if ( !maListOfRules.contains(rule) )
			maListOfRules.add( rule );
	}

	
	/**
	 * Appends a new rule to the policy.
	 */
	public void append( SecurityRuleOpEnum op, 
				        String ref, 
				        SecurityRuleRightsEnum rights )
	{
		append( new SecurityRule(op,ref,rights) );
	}

	
	/**
	 * Removes a rule from the policy
	 */
	public void remove( int id ) {
		maListOfRules.remove( id );
	}

	
	/**
	 * Evaluates every security rule
	 */
	public void eval() throws SecurityException {
		for ( SecurityRule sr : maListOfRules ) {
			if ( sr.eval() )
				return;
		}
	}

}
