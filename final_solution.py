"""
Maximum Sum Subarray with Unique Elements

Problem: Given an integer array nums, you can delete any number of elements 
without making it empty. After deletions, select a subarray where all elements 
are unique and maximize the sum.

Solution Approach:
- For each possible starting position, extend the subarray as far as possible
  while maintaining unique elements
- Track the maximum sum encountered across all valid subarrays
- Time Complexity: O(n²) where n is the length of the array
- Space Complexity: O(n) for the set to track unique elements
"""


def maxSumSubarray(nums):
    """
    Find the maximum sum of a subarray with unique elements.
    
    The key insight is that we can consider all possible subarrays that contain
    only unique elements, and return the one with maximum sum.
    
    Args:
        nums (List[int]): Array of integers
        
    Returns:
        int: Maximum sum of subarray with unique elements
        
    Time Complexity: O(n²) - we examine each starting position and extend
    Space Complexity: O(n) - for the set to track unique elements
    
    Examples:
        >>> maxSumSubarray([1, 2, 3, 4, 5])
        15
        >>> maxSumSubarray([1, 2, 1, 3, 4])
        10
        >>> maxSumSubarray([1, 1, 1, 1])
        1
    """
    if not nums:
        return 0
    
    n = len(nums)
    max_sum = float('-inf')
    
    # Try each starting position
    for start in range(n):
        seen = set()
        current_sum = 0
        
        # Extend the subarray from this starting position
        for end in range(start, n):
            # Stop if we encounter a duplicate
            if nums[end] in seen:
                break
            
            # Add current element to our subarray
            seen.add(nums[end])
            current_sum += nums[end]
            
            # Update maximum sum found so far
            max_sum = max(max_sum, current_sum)
    
    return max_sum


def main():
    """
    Test the solution with various test cases.
    """
    test_cases = [
        # (input, expected_output, description)
        ([1, 2, 3, 4, 5], 15, "All unique elements"),
        ([1, 2, 1, 3, 4], 10, "Some duplicates - multiple valid subarrays"),
        ([1, 1, 1, 1], 1, "All elements same - only single element subarrays"),
        ([5, 2, 1, 2, 5, 2, 1, 2, 5], 8, "Many duplicates - best is [5,2,1]"),
        ([-1, -2, -3], -1, "All negative - pick the least negative"),
        ([1, 2, 3, 2, 1, 4, 5], 15, "Complex case - [3,2,1,4,5] gives maximum"),
        ([4, 2, 4, 5, 6], 17, "Best subarray is [2,4,5,6]"),
        ([10], 10, "Single element"),
        ([-5, -3, -1], -1, "All negative - pick best single element"),
        ([1, 2, 3, 1, 2, 3], 6, "Repeating pattern - [1,2,3] is optimal"),
    ]
    
    print("Testing Maximum Sum Subarray with Unique Elements:")
    print("=" * 55)
    
    all_passed = True
    for i, (nums, expected, description) in enumerate(test_cases, 1):
        result = maxSumSubarray(nums)
        status = "PASS" if result == expected else "FAIL"
        
        if status == "FAIL":
            all_passed = False
            
        print(f"Test {i:2d}: {status}")
        print(f"  Input: {nums}")
        print(f"  Expected: {expected}, Got: {result}")
        print(f"  Description: {description}")
        print()
    
    print("=" * 55)
    print(f"Overall Result: {'ALL TESTS PASSED' if all_passed else 'SOME TESTS FAILED'}")


if __name__ == "__main__":
    main()