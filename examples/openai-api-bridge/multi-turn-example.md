# Multi-Turn Response Example

This demonstrates what the OpenAI API Bridge returns when OpenCode performs multi-turn actions.

## Request
```bash
curl -X POST http://localhost:8083/v1/chat/completions \
  -H "Content-Type: application/json" \
  -d '{
    "model": "gpt-4",
    "messages": [
      {"role": "user", "content": "Write a Python script to calculate prime numbers and test it"}
    ]
  }'
```

## Response (with full multi-turn context)

```json
{
  "id": "chatcmpl-123456789",
  "object": "chat.completion",
  "created": 1736511360,
  "model": "gpt-4",
  "choices": [{
    "index": 0,
    "message": {
      "role": "assistant",
      "content": "I'll create a Python script to calculate prime numbers and test it for you.\n\nFirst, let me create a Python file with a function to check for prime numbers and generate a list of primes.\n\n### Tool Execution: create_file\n```\nStatus: Completed\nTitle: Creating prime number calculator\nInput: File: primes.py\ndef is_prime(n):\n    \"\"\"Check if a number is prime.\"\"\"\n    if n < 2:\n        return False\n    for i in range(2, int(n ** 0.5) + 1):\n        if n % i == 0:\n            return False\n    return True\n\ndef get_primes(limit):\n    \"\"\"Generate all prime numbers up to a limit.\"\"\"\n    primes = []\n    for num in range(2, limit + 1):\n        if is_prime(num):\n            primes.append(num)\n    return primes\n\nif __name__ == \"__main__\":\n    # Test the functions\n    print(\"First 20 prime numbers:\")\n    primes = get_primes(100)\n    print(primes[:20])\n    \n    # Test individual numbers\n    test_numbers = [2, 3, 4, 17, 25, 29, 100]\n    for num in test_numbers:\n        print(f\"{num} is prime: {is_prime(num)}\")\n```\n\nNow let me run the script to test it:\n\n### Tool Execution: shell\n```\nStatus: Completed\nTitle: Running prime number calculator\nInput: $ python3 primes.py\nOutput:\nFirst 20 prime numbers:\n[2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71]\n2 is prime: True\n3 is prime: True\n4 is prime: False\n17 is prime: True\n25 is prime: False\n29 is prime: True\n100 is prime: False\nExit code: 0\n```\n\nExcellent! The script is working correctly. Let me also create a simple unit test to verify the functions:\n\n### Tool Execution: create_file\n```\nStatus: Completed\nTitle: Creating unit tests for prime calculator\nInput: File: test_primes.py\nimport unittest\nfrom primes import is_prime, get_primes\n\nclass TestPrimeFunctions(unittest.TestCase):\n    \n    def test_is_prime(self):\n        # Test prime numbers\n        self.assertTrue(is_prime(2))\n        self.assertTrue(is_prime(3))\n        self.assertTrue(is_prime(5))\n        self.assertTrue(is_prime(7))\n        self.assertTrue(is_prime(11))\n        self.assertTrue(is_prime(13))\n        \n        # Test non-prime numbers\n        self.assertFalse(is_prime(0))\n        self.assertFalse(is_prime(1))\n        self.assertFalse(is_prime(4))\n        self.assertFalse(is_prime(6))\n        self.assertFalse(is_prime(9))\n        self.assertFalse(is_prime(10))\n        \n    def test_get_primes(self):\n        # Test small limits\n        self.assertEqual(get_primes(10), [2, 3, 5, 7])\n        self.assertEqual(get_primes(20), [2, 3, 5, 7, 11, 13, 17, 19])\n        \n        # Test edge cases\n        self.assertEqual(get_primes(1), [])\n        self.assertEqual(get_primes(2), [2])\n        \n        # Test that all returned numbers are actually prime\n        primes_100 = get_primes(100)\n        for prime in primes_100:\n            self.assertTrue(is_prime(prime))\n\nif __name__ == '__main__':\n    unittest.main()\n```\n\n### Tool Execution: shell\n```\nStatus: Completed\nTitle: Running unit tests\nInput: $ python3 -m unittest test_primes -v\nOutput:\ntest_get_primes (test_primes.TestPrimeFunctions) ... ok\ntest_is_prime (test_primes.TestPrimeFunctions) ... ok\n\n----------------------------------------------------------------------\nRan 2 tests in 0.001s\n\nOK\nExit code: 0\n```\n\nPerfect! I've successfully created a Python script to calculate prime numbers with the following features:\n\n1. **`is_prime(n)`** - Function to check if a single number is prime\n2. **`get_primes(limit)`** - Function to generate all prime numbers up to a given limit\n3. **Test script** - Demonstrates the functionality with examples\n4. **Unit tests** - Comprehensive tests to verify correctness\n\nThe script efficiently checks for prime numbers using the square root optimization, and all tests pass successfully. The code is ready to use!\n\n### Files Modified\n- primes.py\n- test_primes.py"
    },
    "finish_reason": "stop"
  }],
  "usage": {
    "prompt_tokens": 25,
    "completion_tokens": 875,
    "total_tokens": 900
  }
}
```

## Key Features Demonstrated

1. **Multiple Tool Executions**: The response shows OpenCode executing multiple tools:
   - Creating the main Python file
   - Running the script to test it
   - Creating unit tests
   - Running the unit tests

2. **Complete Context**: Every action is captured and formatted:
   - Tool status (Completed/Running/Error)
   - Input parameters (commands, file paths)
   - Output results (stdout, exit codes)
   - File modifications

3. **Readable Format**: Actions are clearly separated with headers and code blocks

4. **OpenAI Compatibility**: The response follows the standard OpenAI API format while including all the multi-turn context in the message content

This is what makes the OpenAI API Bridge powerful - it captures EVERYTHING OpenCode does, not just the final message!