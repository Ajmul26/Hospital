def maximumUniqueSubarray(nums):
    """
    Find the maximum sum of a subarray with unique elements after allowing deletions.
    
    Problem: Given an integer array nums, you can delete any number of elements
    (but keep it non-empty). After deletions, select a contiguous subarray where
    all elements are unique. Return the maximum sum of such a subarray.
    
    Key Insight: Since we can delete elements optimally, this is equivalent to
    finding the maximum sum of unique elements in any contiguous subarray of
    the original array.
    
    Algorithm:
    1. For each possible contiguous subarray [i, j]
    2. Calculate the sum of unique elements (skip duplicates)
    3. Return the maximum sum found
    
    Time Complexity: O(n^3)
    Space Complexity: O(n)
    """
    if not nums:
        return 0
    
    n = len(nums)
    max_sum = float('-inf')
    
    # Try all possible contiguous subarrays
    for i in range(n):
        for j in range(i, n):
            # For subarray nums[i:j+1], find sum of unique elements
            seen = set()
            current_sum = 0
            
            for k in range(i, j + 1):
                if nums[k] not in seen:
                    seen.add(nums[k])
                    current_sum += nums[k]
            
            max_sum = max(max_sum, current_sum)
    
    return max_sum


def maximumUniqueSubarray_optimized(nums):
    """
    Optimized O(n^2) solution.
    
    For each starting position, we maintain a running sum and set of seen elements.
    We process elements one by one, and for each element, we calculate the sum
    of unique elements in the current subarray.
    """
    if not nums:
        return 0
    
    n = len(nums)
    max_sum = float('-inf')
    
    for start in range(n):
        seen = set()
        
        for end in range(start, n):
            # Calculate sum of unique elements in subarray [start:end+1]
            if end == start:
                # First element in this subarray
                seen = {nums[end]}
                current_sum = nums[end]
            else:
                # Add current element if not seen
                if nums[end] not in seen:
                    seen.add(nums[end])
                    current_sum += nums[end]
                # If we've seen it, current_sum stays the same
            
            max_sum = max(max_sum, current_sum)
    
    return max_sum


# Example usage and test cases
if __name__ == "__main__":
    # Test cases
    test_cases = [
        [1, 2, 3, 3, 4, 5],      # Output: 15 (keep [1,2,3,4,5])
        [1, 1, 1, 1],            # Output: 1 (keep one element)
        [-1, -2, -3],            # Output: -1 (best single element)
        [4, 2, 4, 5, 6],         # Output: 17 (keep [4,2,5,6])
        [5, 2, 1, 2, 5, 2, 1],   # Output: 8 (keep [5,2,1])
    ]
    
    print("Testing Maximum Unique Subarray Solution:")
    print("=" * 50)
    
    for i, nums in enumerate(test_cases):
        result1 = maximumUniqueSubarray(nums)
        result2 = maximumUniqueSubarray_optimized(nums)
        
        print(f"Test {i+1}: {nums}")
        print(f"  Basic solution:     {result1}")
        print(f"  Optimized solution: {result2}")
        print(f"  Match: {'✓' if result1 == result2 else '✗'}")
        print()
    
    # Additional examples for clarity
    print("\nDetailed Examples:")
    print("-" * 30)
    
    # Example 1
    nums1 = [1, 2, 3, 3, 4, 5]
    print(f"Input: {nums1}")
    print("Process:")
    print("- Original array: [1, 2, 3, 3, 4, 5]")
    print("- We can delete one 3: [1, 2, 3, 4, 5]")
    print("- Select entire array as subarray: [1, 2, 3, 4, 5]")
    print("- All elements unique, sum = 1+2+3+4+5 = 15")
    print(f"Output: {maximumUniqueSubarray(nums1)}")
    print()
    
    # Example 2
    nums2 = [4, 2, 4, 5, 6]
    print(f"Input: {nums2}")
    print("Process:")
    print("- Consider subarray [4, 2, 4, 5, 6]")
    print("- Unique elements: 4, 2, 5, 6 (skip duplicate 4)")
    print("- Sum = 4+2+5+6 = 17")
    print(f"Output: {maximumUniqueSubarray(nums2)}")