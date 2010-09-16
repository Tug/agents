package epfl.lia.logist.security;


/**
 *
 *
 */
public class SecurityRule
{

	/**
	 * The comparison operator
	 */
	private SecurityRuleOpEnum meOperator = SecurityRuleOpEnum.SROP_ANY;
	

	/**
	 * The reference value to which we compare
	 */
	private String msRefValue = null;

	
	/**
	 * The decision to apply if test passes
	 */
	private SecurityRuleRightsEnum meRights = SecurityRuleRightsEnum.SRRG_DENY;


	/**
	 * Default constructor of the class. Initializes the internal
	 * state of the rule
	 */
	public SecurityRule( SecurityRuleOpEnum op, 
						 String ref, 
						 SecurityRuleRightsEnum rights ) {
		meOperator = op;
		msRefValue = ref;
		meRights = rights;
		
	}	

	
	/**
	 * Evaluates the rule and throws an exception if something
	 * goes wild.
	 */
	public boolean eval() throws SecurityException {

		// retrieves the current thread group
		ThreadGroup l_thrGroup = Thread.currentThread().getThreadGroup();
		if ( l_thrGroup == null )
			throw new SecurityException();

		// retrieves the name of the thread group
		String l_thrGroupID = l_thrGroup.getName();

		// do not send an exception until explicitely told to
		// do so...
		switch( meOperator )  {

			// if the thread group name equals to...
			case SROP_EQ:
				if ( l_thrGroupID.equals(msRefValue) ) {
					if ( meRights == SecurityRuleRightsEnum.SRRG_GRANT )
						return true;
					else throw new SecurityException( "Unauthorized access" );
				}
				break;

			// if the thread group name is different from...
			case SROP_NEQ:
				if ( !l_thrGroupID.equals(msRefValue) && 
					 meRights == SecurityRuleRightsEnum.SRRG_DENY )
					throw new SecurityException( "Unauthorized access" );
				break;

			// if the thread group is any
			case SROP_ANY:
				if ( meRights == SecurityRuleRightsEnum.SRRG_DENY )
					throw new SecurityException( "Unauthorized access" );
		}

		// return 
		return false;
	}
}
